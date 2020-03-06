package lambda;

import java.util.ArrayList;

public class LambdaTest {

    public static void main(String[] args) {
        sayHellow("Lambda表达式", str -> System.out.println(str));
    }

    public static void sayHellow(String str , MyLambdaInterface lambdaInterface) {
        lambdaInterface.sayhello(str);
    }
}
