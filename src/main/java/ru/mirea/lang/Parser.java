package ru.mirea.lang;

import ru.mirea.lang.Roman.RomanParser;
import ru.mirea.lang.ast.*;

import java.util.*;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;
    static Map<String, Integer> scope = new TreeMap<>();

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private void error(String message) {
        if (pos < tokens.size()) {
            Token t = tokens.get(pos);
            throw new RuntimeException(message + " в позиции " + t.pos);
        } else {
            throw new RuntimeException(message + " в конце файла");
        }
    }

    private Token match(TokenType... expected) {
        if (pos < tokens.size()) {
            Token curr = tokens.get(pos);
            if (Arrays.asList(expected).contains(curr.type)) {
                pos++;
                return curr;
            }
        }
        return null;
    }

    private Token require(TokenType... expected) {
        Token t = match(expected);
        if (t == null)
            error("Ожидается " + Arrays.toString(expected));
        return t;
    }

    private ExprNode parseElem() {
        Token num = match(TokenType.NUMBER);
        if (num != null)
            return new NumberNode(num);
        Token id = match(TokenType.ID);
        if (id != null)
            return new VarNode(id);
        error("Ожидается число или переменная");
        return null;
    }

    public ExprNode parseUnaryOps() {
        Token t;
        while ((t = match(             // helps to recognize infix/postfix operator
                TokenType.NUMBER,
                TokenType.ID,
                TokenType.PRINT,
                TokenType.INC,
                TokenType.DEC)) != null) {
            if (t.type == TokenType.NUMBER || t.type == TokenType.ID) {  // may be infix
                pos--;
                ExprNode e = parseElem();
                if ((t = match(TokenType.INC, TokenType.DEC)) != null) {
                    e = new UnarOpNode(t, e);
                    require(TokenType.SEMICOLON);
                    return e;
                }
            } else {    // may be postfix
                ExprNode e = new UnarOpNode(t, parseElem());
                require(TokenType.SEMICOLON);
                return e;
            }

        }
        throw new IllegalStateException();
    }

    public ExprNode parseBinOps() {
        ExprNode e1 = parseElem();
        Token op;
        if ((op = match(TokenType.LS, TokenType.EQ, TokenType.GR)) != null) {
            ExprNode e2 = parseElem();
            e1 = new BinOpNode(op, e1, e2);
        }
        return e1;
    }

    public ExprNode parseAssign() {
        if (match(TokenType.ID) == null) {
            return null;
        }
        pos--;
        ExprNode e1 = parseElem();
        Token op;
        if ((op = match(TokenType.ASSIGN)) != null) {
            ExprNode e2 = parseElem();
            e1 = new BinOpNode(op, e1, e2);
            require(TokenType.SEMICOLON);
            return e1;
        }
        pos--;
        return null;
    }

    public ExprNode parseWhile() {
        if (match(TokenType.WHILE) != null) {
            ExprNode condition = parseBinOps();
            require(TokenType.DO);
            PipelineNode statements = new PipelineNode();
            while (match(TokenType.DONE) == null) {
                statements.addNode(parseWhile());
            }
            require(TokenType.SEMICOLON);
            return new WhileNode(condition, statements);
        } else {
            ExprNode node = parseAssign();
            if (node != null) {
                return node;
            }
            return parseUnaryOps();
        }
    }

    public ExprNode parseExpression() {
        PipelineNode pipe = new PipelineNode();
        while (pos < tokens.size()) {
            pipe.addNode(parseWhile());
        }
        return pipe;
    }

    public int eval(ExprNode e) {
        if (e instanceof NumberNode) {
            NumberNode num = (NumberNode) e;
            return num.getDecimal();
        } else if (e instanceof VarNode) {
            VarNode var = (VarNode) e;
            if (scope.containsKey(var.id.text)) {
                return scope.get(var.id.text);
            } else {
                System.out.println("Введите значение " + var.id.text + ":");
                String line = new Scanner(System.in).nextLine();
                int value = Integer.parseInt(line);
                scope.put(var.id.text, value);
            }
        } else if (e instanceof UnarOpNode) {
            UnarOpNode uOp = (UnarOpNode) e;
            Integer value = 0;
            switch (uOp.operator.type) {
                case PRINT:
                    System.out.println(eval(uOp.operand));
                    return 0;
                case INC:
                    if (uOp.operand instanceof VarNode) {
                        value = scope.get(((VarNode) uOp.operand).id.text);
                        value++;
                        VarNode var = (VarNode) uOp.operand;
                        scope.put(var.id.text, value);
                    } else if (uOp.operand instanceof NumberNode) {
                        value = eval(uOp.operand);
                        value++;
                    } else {
                        throw new IllegalStateException();
                    }
                    return value;
                case DEC:
                    if (uOp.operand instanceof VarNode) {
                        value = scope.get(((VarNode) uOp.operand).id.text);
                        value--;
                        VarNode var = (VarNode) uOp.operand;
                        scope.put(var.id.text, value);
                    } else if (uOp.operand instanceof NumberNode) {
                        value = eval(uOp.operand);
                        value--;
                    } else {
                        throw new IllegalStateException();
                    }
                    return value;
                default:
                    return 0;
            }
        } else if (e instanceof BinOpNode) {
            BinOpNode bOp = (BinOpNode) e;

            if (bOp.op.type == TokenType.ASSIGN) {
                if (bOp.left instanceof VarNode) {
                    String key = ((VarNode) bOp.left).id.text;
                    int value;
                    if (bOp.right instanceof NumberNode) {
                        value = RomanParser.getDecimal(((NumberNode) bOp.right).number.text);
                        scope.put(key, value);
                        return 0;
                    } else if (bOp.right instanceof VarNode) {
                        String refKey = ((VarNode) bOp.right).id.text;
                        value = scope.get(refKey);
                        scope.put(key, value);
                        return 0;
                    }
                }
                throw new IllegalStateException();
            }

            int l = eval(bOp.left);
            int r = eval(bOp.right);
            switch (bOp.op.type) {
                case LS:
                    return l < r ? 1 : 0;
                case EQ:
                    return l == r ? 1 : 0;
                case GR:
                    return l > r ? 1 : 0;
                default:
                    break;
            }
        } else if (e instanceof PipelineNode) {
            PipelineNode pipe = (PipelineNode) e;
            for (ExprNode node : pipe.getPipeline()) {
                eval(node);
            }
            return 0;
        } else if (e instanceof WhileNode) {
            WhileNode node = (WhileNode) e;
            while (eval(node.condition) == 1) {
                eval(node.innerExpr);
            }
            return 0;
        }
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        String text =
                "x := IV;" +
                "while x < VI do" +
                    "x++;" +
                    "y := III;" +
                    "while y > I do" +
                        "y--;" +
                        "print x;" +
                        "print y;" +
                    "done;" +
                "done;";

        Lexer l = new Lexer(text);
        List<Token> tokens = l.lex();
        tokens.removeIf(t -> t.type == TokenType.SPACE);

        Parser p = new Parser(tokens);
        ExprNode node = p.parseExpression();

        p.eval(node);
    }
}
