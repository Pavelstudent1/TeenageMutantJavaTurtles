package show;

import java.util.Comparator;

public class Main_1
{
    public static void main(String[] args)
    {
        Comparator<String> cs = new Comparator<String>()
        {
            @Override
            public int compare(String o1, String o2)
            {
                return o1.compareTo(o2);
            }
        };
    }
}
