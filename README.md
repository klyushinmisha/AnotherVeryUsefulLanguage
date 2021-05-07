# Language
Simple language interpreter

## Grammar

```
<code> ::= <stmt>; <code> | <stmt>; 
<stmt> ::= <decl-stmt> |
           <while-stmt> |
           <incr-stmt> |
           <decr-stmt> |
           <print-stmt>

<decl-stmt>  ::= <var-name> := <number>
<while-stmt> ::= while <cond-stmt> do <code> done
<cond-stmt>  ::= <var-name> <comp-sign> <number>
<comp-sign>  ::= > | <
<incr-stmt>  ::= <var-name>++
<decr-stmt>  ::= <var-name>--
<print-stmt> ::= print <var-name>
```

## Program example

```
x := IV;
while x < VI do
    x++;
    y := III;
    while y > I do
        y--;
        print x;
        print y;
    done;
done;
```
