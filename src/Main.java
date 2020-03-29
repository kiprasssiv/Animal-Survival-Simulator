/*
    Kipras Sivickas INFO 4
    Simuliacija ar gyvunas isgyvens kelione iki misko per lauka.
    Is pradziu yra saugaunami gyvunai.
    Tada jie paleidziami kelionei iki misko. Medziotojas kiekviena karta sauna su vis skirtingu pataikymo koeficientu.
    Patikrinimas, kiek ju pasieks keliones tiksla.
*/
import java.util.Vector;
import java.util.Random;

public class Main {
    public static void main(String args[]) {
        Field field = new Field(1000);
        ClassicCounter counter = new ClassicCounter();
        Thread thread = new Thread(() -> {
            Random rand = new Random();
            while (!counter.await(10)) {
                if (rand.nextDouble() > 0.7) {
                    field.advance();
                    counter.advance();
                    System.out.println("Animal ID." + field.getLastCaughtAnimal().getId() + " caught");
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
        System.out.println("The survival game starts now");
        field.survivalSimulationStart();
        return;
    }
}

class ClassicCounter{
    private int counter;
    public ClassicCounter(){
        counter = 0;
    }
    synchronized int read(){
        return counter;
    }
    synchronized void advance(){
        counter++;
    }
    synchronized boolean await(int value){
        return value<=counter;
    }
}

class Animal extends Thread {
    public double distance;
    public boolean finished = false;
    public boolean alive = true;
    public double lifeProcentage;
    Random walkedDistance = new Random();
    Random possibilityOfDeath = new Random();
    Animal(double lifeProcentage) {
        this.distance = 0;
        this.lifeProcentage = lifeProcentage;
    }
    public void run() {
        distance += 100 * walkedDistance.nextDouble();
        if(this.lifeProcentage < this.possibilityOfDeath.nextDouble()/5){
            this.alive = false;
        }
    }
    public double getTravelledDistance() {
        return this.distance;
    }
}

class Field {
    public Vector<Animal> animals = new Vector<Animal>();
    public double distanceToForest;
    public int finished = 0;
    public int dead = 0;
    Field(double length) {
        this.distanceToForest = length;
    }
    public void advance() {
        Random lifeExpectancy = new Random();
        animals.add(new Animal(lifeExpectancy.nextDouble()));
    }
    public void survivalSimulationStart() {
        while (true) {
            for (Animal animal : animals) {
                if (!animal.finished && animal.alive) {
                    animal.run();
                    try {
                        animal.join();
                    } catch (InterruptedException ex) {
                        System.out.println(ex);
                    }
                    if(!animal.alive){
                        this.dead++;
                        System.out.println(animal.getId() + " has died!");
                    }
                    if (animal.getTravelledDistance() >= this.distanceToForest && animal.alive) {
                        System.out.println(animal.getId() + " has finished!");
                        animal.finished = true;
                        this.finished++;
                    }
                } else if (this.finished + this.dead== animals.size())
                    return;
            }
        }
    }
    public int read() {
        return animals.size();
    }
    public boolean await(int value) {
        return animals.size() >= value;
    }
    public Animal getLastCaughtAnimal() {
        return animals.lastElement();
    }
}

