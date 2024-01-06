import java.io.*;
import java.util.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Test {

       public static AudioInputStream audioInputStream;
       public static Clip clip;
       public static void main(String[] args)throws Exception {
        Scanner sc=new Scanner(System.in);
         File f=new File("C:\\JAVA\\Music Player\\songs\\bgm.wav");

         System.out.println(f.exists());


         audioInputStream=AudioSystem.getAudioInputStream(f);
        
         clip=AudioSystem.getClip();
         clip.open(audioInputStream);
        

         System.out.println("song length: "+clip.getMicrosecondLength());
         
         play();
         
         boolean repeat=true;
         int choise;
         while(repeat){
             System.out.println("enter your choise");
             choise=sc.nextInt();

             if(choise==-1){
                repeat=false;
             }
         }



       }

       public static void play(){
        clip.start();

        //  System.out.println(clip.getMicrosecondLength());
         long music_length=clip.getMicrosecondLength();
         System.out.println();
         long position=clip.getMicrosecondPosition();
         System.out.println(position);
         while(position!=music_length){
             position=clip.getMicrosecondPosition();
         }

       }
}

class Audio{

}
