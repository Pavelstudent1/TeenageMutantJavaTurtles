import java.util.function.Supplier;

/*
Ctrl+U -> Hello.class.getDeclaredMethods()
 */
public class Hello {
    public static void main(String[] args) {
        Hello hello = new Hello();
        hello.callPPP(() -> hello.ppp());
        System.out.println("test");
    }

    public void callPPP(Supplier<Object> func) {
        func.get();
    }

    public Object ppp() {
//        throw new RuntimeException("AAAAAA!!!!");
        return null;
    }
}
