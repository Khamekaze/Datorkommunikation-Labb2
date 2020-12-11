package com.khamurai.labb2;

public class Main {

    public static void main(String[] args) {
        Thread t1 = new Thread(new TemperatureClient());
        Thread t2 = new Thread(new TemperatureManager());
        Thread t3 = new Thread(new DataLogger());

        //t3 startas först för att kunna prenumerera på topics från dom två andra trådarna
        //t3 subscribar till två topics och disconnectar sedan. Efter 120 sekunder
        //reconnectar den och hämtar alla meddelanden som den har missat
        t3.start();
        //t2 både publicerar och subscribar så vi startar den som nummer 2
        t2.start();
        //t1 startas sist eftersom den inte subscribar till någon topic utan bara
        //publicerar
        t1.start();
    }
}
