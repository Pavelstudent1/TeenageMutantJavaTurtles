package show;

import java.util.Comparator;

public class Main_2
{
    private int a;

    Comparator<String> cs = (o1, o2) -> {
        //int b = a;
        return o1.compareTo(o2);};

    public static void main(String[] args)
    {
        Comparator<String> cs = (o1, o2) ->{
            return o1.compareTo(o2);
        } ;
    }
}
