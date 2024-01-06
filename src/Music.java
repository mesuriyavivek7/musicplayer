//importing for file handling
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
//import for sql queries
import java.sql.*;

import java.util.*;

//import for music features
import javax.naming.spi.DirStateFactory.Result;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;
import javax.xml.transform.Source;



 class App {
    static Scanner sc=new Scanner(System.in);
    static Connection con=null;
    public static void main(String[] args) throws Exception {
        //System.out.println("Hello, World!");

        //scanner class
        sc=new Scanner(System.in);

        //connection with database
        String dburl="jdbc:mysql://localhost:3306/music player";
        String dbuser="root";
        String dbpass="";

        con=DriverManager.getConnection(dburl, dbuser, dbpass);


       if(con!=null){ 

        System.out.println("----------------------------------MY MUSIC PLAYER------------------------------");
        System.out.println("");
        System.out.println("");
        System.out.println("WANT TO 1. REGISTER OR 2. LOGIN (if you are not registered so please first register)"); 
        int auth_choise=sc.nextInt();

        int user_id=-1;
        String user_name=null;

       do{ 
        if(auth_choise==1){
           user_id=register();
        }else if(auth_choise==2){
          user_id=login();
        }else{
            System.out.println("PLEASE ENTER THE RIGHT CHOISE");
            System.out.println();
            System.out.println("WANT TO 1. REGISTER OR 2. LOGIN (if you are not registered so please first register)"); 
            auth_choise=sc.nextInt();

        }
       }while(user_id==-1);


       //fetch userdata from user_id

       String sql="select username from usersdata where id=?";
       PreparedStatement pst=con.prepareStatement(sql);
       pst.setInt(1, user_id);
       ResultSet rs=pst.executeQuery();
       if(rs.next()){
        user_name=rs.getString(1);
       }
       
       boolean repeat=true;
       int music_id=-1;
       System.out.println("------------------------------------WELCOME TO MY MUSIC PLAYER--------------------------------------");

      //music calss object declaration
       Music mc=new Music();
       mc.setvar(con);
       mc.setplaylist(con, user_id);

       do{
       //initial interface of mymusic player

       System.out.println("");
       System.out.println("");
       System.out.println("  USERNAME: "+user_name);
       System.out.println("  PLAYLIST: "+mc.playlist_name);
       System.out.println("");
       System.out.println("");
       //declare music name
       String music_name=" ";
       if(music_id!=-1){ 
       //fetch music details
      
       String fetch_musicname_sql="select music_name from musicsdata where music_id=?";
       PreparedStatement fetch_musicname_pst=con.prepareStatement(fetch_musicname_sql);
       fetch_musicname_pst.setInt(1,music_id);
       ResultSet fetch_musicname_rs=fetch_musicname_pst.executeQuery();
       if(fetch_musicname_rs.next()){
          music_name=fetch_musicname_rs.getString("music_name");
       }

       System.out.println("  CURRENT SONG PLAYING: "+music_name);
       System.out.println("  11.PREV  12.PAUSE  13.NEXT");
       System.out.println("");
       System.out.println("");
       }
       System.out.println("  1. SHOW MUSICS");
       System.out.println("  2. SEARCH MUSICS");
       System.out.println("  3. MY PLAYLIST");
       System.out.println("  4. EXIT FROM MUSIC PLAYER");
       System.out.println("");

       System.out.print("  ENTER YOUR CHOISE: ");
       int music_step=sc.nextInt();

      
      
       switch(music_step){
          
           case 1:
           music_id=mc.showmusics(con);
           break;


           case 2:
           music_id=mc.searchmusic(con);
           break;

           case 3:
           mc.createplaylist(con,user_id);
           break;

           case 4:
           System.out.println("  EXITED FROM MUSIC PLAYER");
           repeat=false;
           break;

           case 11:
                 mc.setprevpath();
           break;

           case 12:
           mc.pause_music();
           break;

           case 13:
           mc.setnextpath();
           break;

           default:
           System.out.println();
           System.out.println("  PLEASE ENTER RIGHT CHOISE.....!");


       }

     } while(repeat);




       }else{
           System.out.println("CONNECTION FAILED......!");
       }

    }
    static int login()throws Exception{
         int user_id=-1;
         System.out.println("");
         System.out.println("----------------------------------LOGIN----------------------------------");
         System.out.println("");
         System.out.println("");
         System.out.print("  ENTER EMAIL ADDRESS: ");
         String emailaddress=sc.next();
         System.out.print("  ENTER PASSWORD: ");
         String pass=sc.next();

         //fetch data from the database
         String sql="select id from usersdata where email=? and password=?";

         PreparedStatement pst=con.prepareStatement(sql);
         pst.setString(1, emailaddress);
         pst.setString(2, pass);

         ResultSet rs=pst.executeQuery();
         if(rs.next()){
            System.out.println("  YOU ARE LOGGED IN");
           user_id=rs.getInt(1);
         }else{
           System.out.println("  EMAIL ADDRESS OR PASSWORD NOT MATCHED....!");
         }
         

         return user_id;

    }

    static int register()throws Exception{
        System.out.println("");
        System.out.println("");
        System.out.println("-------------------------------REGISTER-----------------------------");
        System.out.println("");
        System.out.println("");
        boolean verify=false;
       
        //variable declarion
        int user_id=-1;
        String username;
        String mobileno;
        String emialaddress;
        String pass;

        //main register do-while loop
        do{ 
        System.out.print("  ENTER USERNAME: ");
        username=sc.next();

        //loop for mobile no checking
         
            do{ 
            
            System.out.print("  ENTER MOBILE NUMBER: ");
            mobileno=sc.next();
            if(mobileno.length()==10){
                int i;
                 for(i=0;i<mobileno.length();i++){
                    char ch=mobileno.charAt(i);
                    int check=ch;
                    if(check<48 || check>57){
                        System.out.println("  PLEASE ENTER VALID MOBIEL NO.......!");
                        System.out.println("");
                        break;
                    }
                 }
                 if(i==10){
                      String checkmobilesql="select mobile from usersdata where mobile="+mobileno;
                      Statement st=con.createStatement();

                      ResultSet rsmobile=st.executeQuery(checkmobilesql);
                      if(rsmobile.next()){
                         System.out.println("  MOBILE NO IS ALREDY TAKEN.....!");
                      }else{
                        verify=true;
                      }
                 }
                 
            }else{
                  System.out.println("  PLEASE ENTER VALID MOBILE NO.....!");
                  System.out.println("");
            }

            }while(verify==false);


        //loop for email address checking
        verify=false;
        
        do{ 
        System.out.print("  ENTER EMAIL ADDRESS: ");
        emialaddress=sc.next();
        int dotcheck=emialaddress.lastIndexOf('.');
        if(dotcheck!=-1){ 
          String ext=emialaddress.substring(emialaddress.lastIndexOf('.'));
          if(ext.equalsIgnoreCase(".com") && emialaddress.length()-emialaddress.lastIndexOf('@')>4){
             String checkemailsql="select email from usersdata where email=?";
             PreparedStatement checkmailpst=con.prepareStatement(checkemailsql);
             checkmailpst.setString(1,emialaddress);
             ResultSet rsemail=checkmailpst.executeQuery();
             if(rsemail.next()){
                 System.out.println("  EMAIL ADDRESS IS ALREDY TAKEN......!");
             }else{
                verify=true;
             }
          }else{
            System.out.println("  PLEASE ENTER VALID EMAIL ADDRESS");
          }
        }else{
            System.out.println("  PLEASE ENTER VALID EMAIL ADDRESS");
        }

        }while(verify==false);

        verify=false;
        //loop  for verify password
        do{ 
        System.out.print("  ENTER THE PASSWORD (password contains maximum 8 character): ");
        pass=sc.next();
          if(pass.length()<=8){
             verify=true;
          }
        }while(verify==false);
        
        verify=false;
        //inserting into database
        String insertsql="insert into usersdata(username,mobile,email,password,playlist_name,playlist_path) values(?,?,?,?,?,?)";
        PreparedStatement insertpst=con.prepareStatement(insertsql);

        insertpst.setString(1,username);
        insertpst.setString(2,mobileno);
        insertpst.setString(3,emialaddress);
        insertpst.setString(4,pass);
        insertpst.setString(5, "null");
        insertpst.setString(6, "null");


        int r=insertpst.executeUpdate();
        System.out.println(r);
        if(r>0){
            String sql="select id from usersdata where mobile=? and email=?";
            PreparedStatement fetchpst=con.prepareStatement(sql);

            fetchpst.setString(1, mobileno);
            fetchpst.setString(2, emialaddress);

            ResultSet getidrs=fetchpst.executeQuery();

            if(getidrs.next()){
               user_id=getidrs.getInt(1);
               System.out.println(user_id);
               System.out.println("You are verified");
               verify=true;
            }else{
                System.out.println("  ERROR OCCURED AT THIS TIME......!");
                
            }
        }
        
        }while(verify==false);

        System.out.println("  REGISTERED SUCESSFULLY");
        return user_id;
    }
}



class Music{
    //declare music features object
    MusicFeatures mf=new MusicFeatures();
    //variable declarion
    boolean playlist_created=false;
    Scanner sc=new Scanner(System.in);
    int total_musics=1;
    Map<Integer,Music_details> md_arr=new HashMap<>();
    
    //initialize of playlist
    Queue<Music_details> my_playlist=new LinkedList<>();
    String playlist_name="none";
    String playlist_path;


    public void pause_music()throws UnsupportedAudioFileException, IOException, 
                                            LineUnavailableException  {
      mf.pause();
    }

    public void setplaylist(Connection con,int user_id)throws Exception{
          
             //fetch playlist file path
             String fetch_playlist_file="select playlist_name , playlist_path from usersdata where id=?";
             PreparedStatement fetch_playlist_pst=con.prepareStatement(fetch_playlist_file);
             fetch_playlist_pst.setInt(1, user_id);
             ResultSet fetch_playlist_rs=fetch_playlist_pst.executeQuery();
             if(fetch_playlist_rs.next()){
                  playlist_name=fetch_playlist_rs.getString(1);
                  playlist_path=fetch_playlist_rs.getString(2);
                  if(playlist_name.equals("null") && playlist_path.equals("null")){
                    playlist_created=false;
                  }else{ 
                    playlist_created=true;
                    File f=new File(playlist_path);

                  if(f.exists()){
                       FileReader fr=new FileReader(f);
                       BufferedReader br=new BufferedReader(fr);

                       String get_data=br.readLine();
                       while(get_data!=null){
                          String get_data_arr[]=get_data.split(" ");
                          int music_index=Integer.parseInt(get_data_arr[1]);
                           
                          //fetch that data and store into playlist queue
                          my_playlist.add(md_arr.get(music_index));
                          get_data=br.readLine();
                       }

                  }else{
                     playlist_created=false;
                  }
                }
             }else{
                playlist_created=false;
             }

        
    }

    public void set_updated_playlist(Connection con)throws Exception{
          int playlist_size=my_playlist.size();
          FileReader fr_update=new FileReader(playlist_path);
          BufferedReader br_update=new BufferedReader(fr_update);

          for(int i=0;i<playlist_size;i++){
               br_update.readLine();
          }
          String updated_playlist_data=br_update.readLine();
          while(updated_playlist_data!=null){
              String updated_playlist_arr[]=updated_playlist_data.split(" ");
              int music_index=Integer.parseInt(updated_playlist_arr[1]);
              my_playlist.add(md_arr.get(music_index));
              updated_playlist_data=br_update.readLine();
          }
    }

    public void setvar(Connection con)throws  Exception{
        String sql="select * from musicsdata";
        Statement fetchmusic=con.createStatement();
        ResultSet fetchrs=fetchmusic.executeQuery(sql);

        
        while(fetchrs.next()){
          int music_id=fetchrs.getInt("music_id");
          String music_name=fetchrs.getString("music_name");
          String music_path=fetchrs.getString("music_path");
          float music_size=fetchrs.getFloat("music_size");
          String music_duration=fetchrs.getString("music_duration");

          Music_details md=new Music_details(music_id, music_name, music_path, music_size, music_duration);
          md_arr.put(total_musics,md);
          total_musics++;
        }



    }

    public void setnextpath()throws Exception{
        int currentplayindex=mf.currentplayindex;
        int nextplayindex=-1;
        if(currentplayindex+1>total_musics){
           nextplayindex=1;
        }else{
          nextplayindex=currentplayindex+1;
        }
        mf.setpath(nextplayindex, md_arr.get(nextplayindex).music_path, false);
        mf.play();

    }

    public void setprevpath()throws Exception{
        int currentplayindex=mf.currentplayindex;
        int prevplayindex=-1;
        if(currentplayindex-1<=0){
           prevplayindex=total_musics-1;
        }else{
          prevplayindex=currentplayindex-1;
        } 
        mf.setpath(prevplayindex,md_arr.get(prevplayindex).music_path, false);
        mf.play(); 
    }

    public int showmusics(Connection con){
        if(total_musics>0){
         
           
           System.out.println("----------------------------MUSIC SHOW LIST-----------------------------");
           for(Map.Entry<Integer,Music_details> entry:md_arr.entrySet()){
              System.out.println("");
            
              if(entry.getKey()==1){
                System.out.println("     SR.NO     |     MUSIC NAME     |     SIZE     |     DURATION  ");
              }
              System.out.println("     "+entry.getKey()+"                "+entry.getValue().music_name+"              "+entry.getValue().music_size+"              "+entry.getValue().music_duration);
              System.out.println("");        
              
           }

           System.out.println("");
           System.out.println("");
           boolean music_choise_repeat=true;
           do{ 
           System.out.println("FOR PLAY MUSIC ENTER THE MUSIC SR.NO (else enter the -1 for next process)");
           int music_choise=sc.nextInt();

           if(music_choise!=-1){
                  if(music_choise>0 && music_choise<=total_musics){
                      int music_id=md_arr.get(music_choise).music_id;
                      String music_path=md_arr.get(music_choise).music_path;
                      
                      //play the music
                      try{ 
                      mf.setpath(music_choise, music_path,false);
                      mf.play();
                      }catch(Exception ex){
                        System.out.println(ex.getMessage());
                        System.out.println("ERROR WHILE PLAYING MUSIC.......!");
                      }
                      music_choise_repeat=false;
                      return music_id;
                  }
           }else{
                music_choise_repeat=false;
           }
           }while(music_choise_repeat);
            return -1;
           

        }else{
            System.out.println("  NO ANY MUSIC FOUND.....!");
            return -1;
        }
    }

    public int searchmusic(Connection con){
          System.out.println("");
          System.out.println("----------------------------------------SEARCH MUSICS------------------------------------");
          System.out.println("");
          System.out.print("  ENTER THE MUSIC NAME: ");
          String search_music_name=sc.next();
          System.out.println("");
          int music_count=1;
          System.out.println("  YOUR SEARCH RESULTS.....");
          System.out.println();
          for(Map.Entry<Integer,Music_details> entry:md_arr.entrySet()){
             if(entry.getValue().music_name.contains(search_music_name)){
                System.out.println("");
               

              if(music_count==1){
                System.out.println("     SR.NO     |     MUSIC NAME     |     SIZE     |     DURATION  ");
              }
              System.out.println("     "+entry.getKey()+"                "+entry.getValue().music_name+"              "+entry.getValue().music_size+"              "+entry.getValue().music_duration);
              System.out.println("");        
              music_count++;
             }
          }
          if(music_count==1){

            System.out.println("  NO SUCH SONG FOUNG WITH NAME "+search_music_name);
            return -1;
          }else{
            System.out.println("  FOR PLAY ENTER SONG SR.NO (else enter the -1 for next process)");
            int search_music_choise=sc.nextInt();

            if(search_music_choise!=-1){
                  int music_id=md_arr.get(search_music_choise).music_id;
                  String music_path=md_arr.get(search_music_choise).music_path;
                  try{
                  mf.setpath(search_music_choise, music_path,false);  
                  mf.play(); 
                  return music_id;   
                  }catch(Exception ex){
                    System.out.println("  ERROR WHILE PLAYING MUSIC....!");
                  }
            }
            return -1;
          }

 
    }

    public void createplaylist(Connection con,int user_id)throws Exception{
            System.out.println("");
            if(!playlist_created){ 
            //creation of playlist
            System.out.println("------------------------------------CREATE PLAYLIST--------------------------------------");

            System.out.println("");
            System.out.print("  ENTER THE PLYLIST NAME: ");
            String playlist_name=sc.next();

            String playlistfilename=playlist_name+user_id;
            File f=new File(playlistfilename);
            f.createNewFile();

            String insertplaylist="update usersdata set playlist_name=? , playlist_path=? where id=?";
            PreparedStatement insertplaylist_pst=con.prepareStatement(insertplaylist);
            insertplaylist_pst.setString(1, playlist_name);
            insertplaylist_pst.setString(2, f.getAbsolutePath());
            insertplaylist_pst.setInt(3, user_id);
            int r=insertplaylist_pst.executeUpdate();

            if(r>0){

            
            //for writing song name into playlistfilename
            FileWriter fw=new FileWriter(f,true);
            BufferedWriter bw=new BufferedWriter(fw);

            System.out.println(" ");
            System.out.println("-----------------------------------------SONGS LIST---------------------------------------");
            System.out.println();
            for(Map.Entry<Integer,Music_details> entry:md_arr.entrySet()){
              System.out.println("");
            
              if(entry.getKey()==1){
                System.out.println("     SR.NO     |     MUSIC NAME     |     SIZE     |     DURATION  ");
              }
              System.out.println("     "+entry.getKey()+"                "+entry.getValue().music_name+"              "+entry.getValue().music_size+"              "+entry.getValue().music_duration);
              System.out.println("");        
              
           }
           boolean  checkingnumberformatexception=false;
           do{ 

           System.out.println();
           System.out.print("  ENTER THE SR.NO WITH SPACE SEPERATED FOR ADD SONGS INTO "+playlist_name+" PLAYLIST: ");
           sc.nextLine();
           String playlist_add=sc.nextLine();
           
           String playlist_add_arr[]=playlist_add.split(" ");
           for(int i=0;i<playlist_add_arr.length;i++){
               int music_playlist_chosie=-1;
               
               try{ 
                 music_playlist_chosie=Integer.parseInt(playlist_add_arr[i]);
               }catch(NumberFormatException ex){
                 checkingnumberformatexception=true;
                 System.out.println("  PLEASE ENTER THE SR.NO AS NUMBER.....!");
               }
               if(checkingnumberformatexception==false){ 
               int music_id=md_arr.get(music_playlist_chosie).music_id;
               String music_path=md_arr.get(music_playlist_chosie).music_path;
               String file_writer=music_id+" "+music_playlist_chosie+" "+music_path+"\n";
               bw.write(file_writer);
               
               }
           }
          bw.close();
          System.out.println("");
          System.out.println(" PLAYLIST IS SUCESSFULLY CREATED");

          //set playlist 
           setplaylist(con, user_id);

          }while(checkingnumberformatexception);

        }else{
              
             System.out.println(" THERE IS AN ERROR WHILE CREATING PLAYLIST");
        }

      }

       System.out.println();
       System.out.println();
       System.out.println("--------------------------------"+playlist_name+"-------------------------------");
       boolean playlist_repeat=true;

       do{ 
       System.out.println();
       System.out.println();
       System.out.println("  PLAYLIST: "+playlist_name);
       System.out.println("  TOTAL SONGS: "+my_playlist.size());
       System.out.println();
       System.out.println("  PLAYLIST SONGS: ");
       System.out.println();
       //show playlilst
       int playlist_music_cnt=1;
       for(Music_details fetch_md: my_playlist){
              if(playlist_music_cnt==1){
                System.out.println("     SR.NO     |     MUSIC NAME     |     SIZE     |     DURATION  ");
              }
              System.out.println("     "+playlist_music_cnt+"                "+fetch_md.music_name+"              "+fetch_md.music_size+"              "+fetch_md.music_duration);
              System.out.println(""); 
              playlist_music_cnt++;     
       }
       
       System.out.println("");
       System.out.println("  1. PLAY YOUR PLAYLIST");
       System.out.println("  2. ADD SONGS INTO PLAYLIST");
       System.out.println("  3. REMOVE SONG FROM PLAYLIST");
       System.out.println("  4. EXIT FROM PLAYLIST");
       System.out.println();
       System.out.print("  ENTER YOUR CHOISE: ");
       int playlist_choise=sc.nextInt();

       if(playlist_choise==1){
          for(Music_details play_music:my_playlist){
              mf.setpath(play_music.music_id, play_music.music_path,true);
              System.out.println("");
              System.out.println("  CURRENT PLAY: "+play_music.music_name);
              mf.play_playlist();
          }
       }else if(playlist_choise==2){
           //for adding song into playlist
          
           //SET FILE FOR WRITTING
           FileWriter fw_add=new FileWriter(playlist_path,true);
           FileReader fr_add=new FileReader(playlist_path);
           BufferedWriter bw_add=new BufferedWriter(fw_add);
           BufferedReader br_add=new BufferedReader(fr_add);

          System.out.println(" ");
            System.out.println("-----------------------------------------SONGS LIST---------------------------------------");
            System.out.println();
            for(Map.Entry<Integer,Music_details> entry:md_arr.entrySet()){
              System.out.println("");
            
              if(entry.getKey()==1){
                System.out.println("     SR.NO     |     MUSIC NAME     |     SIZE     |     DURATION  ");
              }
              System.out.println("     "+entry.getKey()+"                "+entry.getValue().music_name+"              "+entry.getValue().music_size+"              "+entry.getValue().music_duration);
              System.out.println("");        
              
           }
           boolean  checkingnumberformatexception=false;
           boolean check_music_id=false;
           do{ 

           System.out.println();
           System.out.print("  ENTER THE SR.NO WITH SPACE SEPERATED FOR ADD SONGS INTO "+playlist_name+" PLAYLIST: ");
           sc.nextLine();
           String playlist_add=sc.nextLine();
           
           String playlist_add_arr[]=playlist_add.split(" ");
           
           for(int i=0;i<playlist_add_arr.length;i++){
               int music_playlist_chosie=-1;
               check_music_id=false;
               try{ 
                 music_playlist_chosie=Integer.parseInt(playlist_add_arr[i]);
               }catch(NumberFormatException ex){
                 checkingnumberformatexception=true;
                 System.out.println("  PLEASE ENTER THE SR.NO AS NUMBER.....!");
               }
               if(checkingnumberformatexception==false){ 
               int music_id=md_arr.get(music_playlist_chosie).music_id;
               String check_playlist_data=br_add.readLine();
               while(check_playlist_data!=null){
                  String check_playlist_arr[]=check_playlist_data.split(" ");
                  int contain_music_id=Integer.parseInt(check_playlist_arr[0]);
                  if(contain_music_id==music_id){
                     check_music_id=true;
                     break;
                  }
                  check_playlist_data=br_add.readLine();
               }

               if(check_music_id==true){
                 System.out.println("  "+md_arr.get(music_playlist_chosie).music_name+" IS ALREDY EXISTS IN YOUR PLAYLIST");
                 System.out.println("");
               }else{
                 String music_path=md_arr.get(music_playlist_chosie).music_path;
                 String file_writer=music_id+" "+music_playlist_chosie+" "+music_path+"\n";
                 bw_add.write(file_writer);
                 System.out.println("  "+md_arr.get(music_playlist_chosie).music_name+" IS ADDED INTO YOUR PLAYLIST");
                 System.out.println("");
               }
              
               
               }
           }
          bw_add.close();
          br_add.close();
          System.out.println("");

          //set playlist 
          set_updated_playlist(con);

          }while(checkingnumberformatexception);
         

       }else if(playlist_choise==3){

           BufferedReader br_remove=new BufferedReader(new FileReader(playlist_path));
           System.out.print("  ENTER THE SR.NO WITH SPACE SEPERATED WHICH SONG DO YOU WANT TO REMOVE FROM YOUR PLAYLIST: ");
           sc.nextLine();
           String playlist_remove=sc.nextLine();
           
           
           String playlist_remove_arr[]=playlist_remove.split(" ");
           String playlist_main_data[]=new String[my_playlist.size()-playlist_remove_arr.length];
          

           if(playlist_remove_arr.length>my_playlist.size()){
             System.out.println("");
             System.out.println("  ENTERED TOTAL MUSIC NO IS GREATER THAN AVAILABLE PLAYLIST SONGS...!");
           }else if(playlist_remove_arr.length==my_playlist.size()){
               my_playlist.clear();
              // fr_remove.close();
               System.out.println("");
               System.out.println("  NOW YOUR WHOLE PLAYLIST IS EMPTY");
           }else{
              int index=0;
              for(int i=1;i<=my_playlist.size();i++){
                 boolean add=true;
                 String playlist_data=br_remove.readLine();
                 for(int j=0;j<playlist_remove_arr.length;j++){
                     int music_index=Integer.parseInt( playlist_remove_arr[j]);
                     if(music_index==i){
                        add=false;
                        break;
                     }
                 }
                 if(add==true){
                    playlist_main_data[index]=playlist_data;
                    index++;
                 }
                 playlist_data=br_remove.readLine();
              }
              br_remove.close();

               FileWriter fr_remove=new FileWriter(playlist_path,false);
               BufferedWriter bw_remove=new BufferedWriter(fr_remove);
             
              for(int i=0;i<playlist_main_data.length;i++){
                 bw_remove.write(playlist_main_data[i]+"\n");
              }
              bw_remove.close();
              my_playlist.clear();

              //reset whole playlist
              setplaylist(con, user_id);
              System.out.println();
              System.out.println("  REMOVED GIVEN SONG SUCESSFULLY");

           }

           
       }else if(playlist_choise==4){
           System.out.println("  YOU ARE EXITED FROM YOUR PLAYLIST");
           playlist_repeat=false;
       }
       
      }while(playlist_repeat);

    }

     
}

class MusicFeatures{
    
    //variable declarion
    String status="pause";
    Clip clip;
    long currentFrame;
    AudioInputStream audioInputStream;
    int currentplayindex=-1;
    String file_path;
    public void setpath(int music_index,String music_path,boolean isplaylist)throws UnsupportedAudioFileException,
        IOException, LineUnavailableException{
        this.file_path=music_path;
        File music_file=new File(music_path);
        if(music_file.exists()){
           if(status.equals("play")){
             clip.stop();
             clip.close();
           }
           audioInputStream=AudioSystem.getAudioInputStream(music_file);
           clip=AudioSystem.getClip();
           clip.open(audioInputStream);
           currentplayindex=music_index;
           if(!isplaylist){ 
           clip.loop(Clip.LOOP_CONTINUOUSLY);
           }
        }else{
            System.out.println("  SORRY BUT SONG IS NOT FOUND........!");
        }
              
    }

    public void play(){
     
       clip.start();
       status="play";
        
    }
    public void play_playlist(){
       status="play";
      
       clip.start();
       long music_length=clip.getMicrosecondLength();
       currentFrame=clip.getMicrosecondPosition();
       while(currentFrame!=music_length){
          currentFrame=clip.getMicrosecondPosition();
       }
    }

    public void pause()throws UnsupportedAudioFileException, IOException, 
                                            LineUnavailableException  {
           if(status.equals("play") && clip.isOpen()){
              currentFrame=clip.getMicrosecondPosition();
              clip.stop();
              status="pause";
           }else{
              clip.close();
              resetAudioStream(this.file_path);
              clip.setMicrosecondPosition(currentFrame);
              this.play();
           }
    }

     public void resetAudioStream(String file_path) throws UnsupportedAudioFileException, IOException, 
                                            LineUnavailableException  
    { 
        audioInputStream = AudioSystem.getAudioInputStream(new File(file_path)); 
        clip.open(audioInputStream); 
        clip.loop(Clip.LOOP_CONTINUOUSLY); 
    } 
    
}

class Music_details{
    int music_id;
    String music_name;
    String music_path;
    float music_size;
    String music_duration;
    String music_file_path;


    public Music_details(int music_id,String music_name,String music_path,float music_size,String music_duration){
        this.music_id=music_id;
        this.music_name=music_name;
        this.music_path=music_path;
        this.music_duration=music_duration;
        this.music_size=music_size;
       
    }
}

