import java.util.LinkedList;
public class Main{
  public static void main(String[] args){
    LinkedList<String> cars= new LinkedList<String>();
    cars.add("Volvo");
    cars.add("BMW");
    cars.add("Frod");
    cars.add("Mazda");
    for(int i=0; i<cars.size();i++){
       System.out.println(cars.get(i));
    }
  }
}
