import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import java.applet.*;
import java.awt.*;
import javax.swing.*;

/*  Creators: Ana Andre and Lisa Guo 
    START WITH 1000 HP
    EGG SHOOTER - SHOOTS EGGS AT ENEMIES 
                - UNLIMITED AMMO
                - IF SHOT AT ANY CARROT, CARROT DISAPPEARS
  
    6 VARIATIONS OF EGG PATTERN - CAN BE CHANGED IN PAUSE MENU
    
    4 LEVELS(STORY MODE):
        LEVEL 1 - CARROTS + CARROT MONSTERS
        LEVEL 2 - CARROTS + CARROT MONSTERS + BEAR TRAPS
        LEVEL 3 - CARROTS + CARROT MONSTERS + BEAR TRAPS + BOSS [FARMER]
        LEVEL 4 - CARROTS + CARROT MONSTERS + BEAR TRAPS + BOSS [FARMER WITH ABILITY TO THROW PITCH FORK]
 
    10 LEVELS(SURVIVAL MODE):
          LEVEL 1 - CARROTS + CARROT MONSTERS 
          LEVEL 2 - CARROTS + CARROT MONSTERS + BEAR TRAPS
          LEVEL 3 - CARROTS + CARROT MONSTERS + BEAR TRAPS + POISON/GOLDEN CARROTS
          LEVEL 4 - SCROLL RIGHT
          LEVEL 5 - CONFUSION, UP <-> DOWN, RIGHT <-> LEFT
          LEVEL 6 - LOSE ABILITY TO SHOOT, TRAP PATTERN (COLUMN FILLED WITH TRAPS WITH ONE OPENING)
          LEVEL 7 - REGAIN ABILITY TO SHOOT, TRAP PATTERN (COLUMN FILLED WITH TRAPS WITH ONE CARROT MONSTER)
          LEVEL 8 - MOVE NORMALLY, SCROLL LEFT, ALTERNATE PATTERN (TRAP,CARROT,TRAP,CARROT,TRAP - CARROT,TRAP,CARROT,TRAP,CARROT)
          LEVEL 9 - NO PATTERN, FAST SPEED
          LEVEL 10 - NO PATTERN, SUPER FAST SPEED, NO CARROTS GENERATED, MORE BEAR TRAPS
        
    ENEMY INFORMATION:    
    -CARROT MONSTER(S): ATTACK UPON CONTACT, VULNERABLE TO ATTACK
    -BEAR TRAP(S): SNAP UPON CONTACT, INVULNERABLE TO ATTACK
    -FARMER: MOVES UP AND DOWN, THROWS PITCH FORK, VULNERABLE TO ATTACK
 
    ITEM INFORMATION:
    -CARROT: DROPPED BY ENEMIES OR FOUND ON GRASS, 20 CARROTS = 500HP, 10 POINTS EACH CARROT
    -POISON CARROT: INFLICTS POISON [GENERATED ONLY WHEN NOT POISONED]
    -GOLDEN CARROT: HEALS POISON [GENERATED ONLY WHEN POISONED]
  
    3 DIFFICULTY LEVELS:
    -EASY:   CARROT MONSTER -50HP
             BEAR TRAP -100HP
             POISON -15HP/SECOND
             20 CARROTS = 500HP
             FARMER -250HP
 
    -NORMAL: CARROT MONSTER -150HP
             BEAR TRAP -200HP
             POISON -30HP/SECOND
             20 CARROTS = 300HP
             FARMER -350HP
 
    -HARD:   CARROT MONSTER -250HP
             BEAR TRAP -300HP
             POISON -60HP/SECOND
             20 CARROTS = 100HP
             FARMER -450HP
 
    SOUND TUTORIAL: http://www.dreamincode.net/forums/topic/14083-incredibly-easy-way-to-play-sounds/
    
    IMAGE COLLECTIONS: [CONCEPT ART] - http://imgur.com/a/nHN7a 
                       [FINISHED SPRITES WITHOUT MIRROR] - http://imgur.com/a/pauQc 
*/
public class Game extends JApplet
{
  private Grid grid;
  
  // Location of the user
  private int userRow;
  private int userCol;
  
  private int msElapsed; // used to hold measure of time
  
  // Counts number of times a carrot is gained/bunny is attacked
  private int timesGet;
  private int timesAvoid;
  
  // Location of the Farmer
  private int farmerRow; 
  private int farmerCol; 
  
  private boolean gameWon; // checks if game has been won
  private int timesHitFarmer; //counts the amount of times the farmer has been hit by the user
  
  //User varaibles
  private int remainingLife;  
  private boolean receiveHit;  
  private boolean hitTrap;  
  private int countGet;
  private int altPattern; //Used for Level Four
  private int killStreak;
  private int highestKillStreak;
  private boolean isPoisoned;
  private String userImage;
  private boolean mirror; //Used for mirroring Sprites
  private String difficulty;
  private String gameMode;
  
  private String eggType;
  private AudioClip currentAudio;
  
  public Game()
  {
    grid = new Grid(6,11);
    userRow = 0;
    userCol = 0; 
    farmerRow = 0;  
    farmerCol = grid.getNumCols()-1;  
    msElapsed = 0;
    timesGet = 0;
    timesAvoid = 0;
    timesHitFarmer = 0;
    remainingLife = 1000;  
    receiveHit = false;   
    hitTrap = false;  
    gameWon = false;  
    isPoisoned = false;
    countGet = 0;     
    altPattern = 0;
    killStreak = 0;
    highestKillStreak = 0;
    updateTitle();
    difficulty = "normal";
    eggType = "bee";
    userImage = "images/user.gif";
  }
  
    public void play()
    {
            //Title Screen
            AudioClip titleSong = Applet.newAudioClip(Game.class.getResource("audio/TitleSong.wav"));
            ImageIcon titleIcon = new ImageIcon(Game.class.getResource("images/TitleScreen.png"));

            titleSong.loop();
            Object[] start = {"STORY MODE","SURVIVAL MODE"};

            String instructions = "Welcome! This is the story of a bunny \nwho lived near a farm from which "+
                    "it \ntook delicious carrots. One day, the \nfarmer was fed up with it and cooked \nup a pesticide "+
                    "to get rid of the \nmischievous rabbit. It all went wrong and \nsoon everything in the land was\n"+
                    "infected. The rabbit grew as tall as a \nbear and started to speak, the carrots \nreceived faces and "+
                    "formed appendages, \nand the farmer turned outright evil! \nBring in those carrots while you can, soldier!"+
                    "\n...\nEvery 20 carrots gives you HP!\nThe egg shooter has the power to restore the monster carrots,\nhowever"+
                    " it can reduce normal carrots to ashes.\nBe careful.\n...\nUse the [Arrow Keys] to move around the field.\nPress [S] to "+
                    "use your egg shooter.\nPress [P] to pause.\nGood Luck!\n...\nSTORY MODE:\nFight and defeat the evil farmer who lies at the end of the farm.\n"+
                    "\nSURVIVAL MODE:\nSee how long you can last as you fight through waves of monsters and traps,\nas well as poisonous carrots that stand in your way.\nThis mode is an endless fight through "+
                    "many levels. \nThose who choose this level are in for a challenge.";

            int choice = JOptionPane.showOptionDialog(null, instructions,"MAIN MENU",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE, titleIcon, start, null);


        //Initializing audio clips
        AudioClip ambientMusic = Applet.newAudioClip(Game.class.getResource("audio/ambientMusic.wav"));
        AudioClip bossTheme = Applet.newAudioClip(Game.class.getResource("audio/bossBattle.au"));
        AudioClip newLevel = Applet.newAudioClip(Game.class.getResource("audio/levelPassed.wav"));

        //Start Story Mode
        if(choice == 0)
        {
            gameMode = "story";
            Object[] difficulties = {"Easy","Normal","Hard"};
            int diffLevel = JOptionPane.showOptionDialog(null, "Please choose a difficulty level.","Difficulty",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE, null, difficulties, null);
            if(diffLevel == 0)
                difficulty = "easy";
            else if(diffLevel == 2)
                difficulty = "hard";
            grid.setImage(new Location(userRow, 0), userImage);
            titleSong.stop();
            ambientMusic.loop();
            currentAudio = ambientMusic;

            //Variable used for Level Four
            boolean doOnce = true;
            while (!isGameOver())
            {
                grid.pause(100);
                handleKeyPress();

                //LEVEL ONE
                if(msElapsed < 20000)
                {
                    if (msElapsed % 300 == 0)
                    {
                        scrollLeft(); // <---
                        populateRightEdge();
                    }
                    updateTitle();
                    msElapsed += 100;
                }

                //LEVEL TWO
                else if(msElapsed<40000) 
                {
                    if(msElapsed == 20000)
                    {

                        newLevel.play();

                        ImageIcon farmerDialogue=new ImageIcon(Game.class.getResource("images/farmerDialogBoxLvl2.png"));
                        ImageIcon bunnyDialogue = new ImageIcon(Game.class.getResource("images/bunnyDialogBoxLvl2.png"));
                        Object[] options = { "Continue" };
                        JOptionPane.showOptionDialog(null,"","Farmer",JOptionPane.YES_OPTION,JOptionPane.INFORMATION_MESSAGE, farmerDialogue, options, null);
                        JOptionPane.showOptionDialog(null, "","Bunny",JOptionPane.YES_OPTION,JOptionPane.INFORMATION_MESSAGE, bunnyDialogue, options, null);
                    }
                    if (msElapsed % 300 == 0)
                    {
                        scrollLeft();
                        populateRightEdge();
                    }
                    updateTitle();
                    msElapsed += 100;
                }

                //LEVEL THREE
                else 
                {
                    grid.setImage(new Location(farmerRow,farmerCol), "images/farmer.png");
                    moveFarmer();
                    if(msElapsed == 40000)
                    {

                        //Setting Boss Theme
                        ambientMusic.stop();
                        newLevel.play();
                        bossTheme.loop();
                        currentAudio = bossTheme;

                        ImageIcon farmerDialogue2=new ImageIcon(Game.class.getResource("images/farmerDialogBoxLvl3.png")); 
                        ImageIcon bunnyDialogue2 = new ImageIcon(Game.class.getResource("images/bunnyDialogBoxLvl3.png"));
                        Object[] options = { "Continue" };
                        JOptionPane.showOptionDialog(null, "","Farmer",JOptionPane.YES_OPTION,JOptionPane.INFORMATION_MESSAGE, farmerDialogue2, options, null);
                        JOptionPane.showOptionDialog(null,"","Bunny",JOptionPane.YES_OPTION,JOptionPane.INFORMATION_MESSAGE,bunnyDialogue2,options,null);
                    }

                    //LEVEL FOUR
                    if(timesHitFarmer >= 5)
                    {
                        if(doOnce == true)
                        {
                            ImageIcon farmerDialogue3=new ImageIcon(Game.class.getResource("images/farmerDialogBoxLvl4.png")); 
                            ImageIcon bunnyDialogue3 = new ImageIcon(Game.class.getResource("images/bunnyDialogBoxLvl4.png"));
                            Object[] options = { "Continue" };
                            JOptionPane.showOptionDialog(null, "","Farmer",JOptionPane.YES_OPTION,JOptionPane.INFORMATION_MESSAGE, farmerDialogue3, options, null);
                            JOptionPane.showOptionDialog(null,"","Bunny",JOptionPane.YES_OPTION,JOptionPane.INFORMATION_MESSAGE,bunnyDialogue3,options,null);

                            doOnce = false;
                        }
                        if(msElapsed % 1000 == 0)
                        {
                            throwFork();
                        }
                    }    

                    if (msElapsed % 300 == 0)
                        scrollLeft();

                    if (msElapsed % 1500 == 0)
                    {
                        bossPattern();
                    }
                    updateTitle();
                    msElapsed += 100;
                }
            }
            //User won
            if(timesHitFarmer==getFarmerHP())
            {
                gameWon = true;
            }

            if(gameWon)
            {
                //VICTORY
                bossTheme.stop();
                victory();
            }
            else
            {
                //DEFEAT
                ambientMusic.stop();
                bossTheme.stop();
                defeat();
            }
        }
        //Start Survival Mode
        else if(choice == 1)
        {
            gameMode = "survival";
            Object[] difficulties = {"Easy","Normal","Hard"};
            int diffLevel = JOptionPane.showOptionDialog(null, "Please choose a difficulty level.","Difficulty",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE, null, difficulties, null);
            if(diffLevel == 0)
                difficulty = "easy";
            else if(diffLevel == 2)
                difficulty = "hard";
            grid.setImage(new Location(userRow, 0), userImage);
            titleSong.stop();
            ambientMusic.loop();
            currentAudio = ambientMusic;

            while (!isGameOver())
            {
                
                grid.pause(100);
                handleKeyPress();

                //LEVEL ONE
                if(msElapsed < 20000)
                {
                    if (msElapsed % 300 == 0)
                    {
                        scrollLeft(); // <---
                        populateRightEdge();
                    }
                    updateTitle();
                    msElapsed += 100;
                }

                //LEVEL TWO
                else if(msElapsed<40000) 
                {
                    if(msElapsed == 20000)
                    {

                        newLevel.play();
                        ImageIcon warning = new ImageIcon(Game.class.getResource("images/bunnyPortrait.png"));
                        Object[] options = { "Continue" };
                        JOptionPane.showOptionDialog(null,"Bear traps incoming! \nBetter watch my step!","Bunny",JOptionPane.YES_OPTION,JOptionPane.INFORMATION_MESSAGE, warning, options, null);
                    }
                    if (msElapsed % 300 == 0)
                    {
                        scrollLeft();
                        populateRightEdge();
                    }
                    updateTitle();
                    msElapsed += 100;
                }
                //LEVEL THREE
                else if(msElapsed<60000) 
                {
                    if(msElapsed == 40000)
                    {

                        newLevel.play();
                        ImageIcon warning = new ImageIcon(Game.class.getResource("images/bunnyPortrait.png"));
                        Object[] options = { "Continue" };
                        JOptionPane.showOptionDialog(null,"Poisoned carrots spotted! Better destroy/avoid those.\nNote: Poisoned Carrots will inflict poison. Golden Carrots will heal poison.","Bunny",JOptionPane.YES_OPTION,JOptionPane.INFORMATION_MESSAGE, warning, options, null);
                    }
                    if(msElapsed % 1000 == 0 && isPoisoned)
                    {
                        poisoned();
                    }
                    if (msElapsed % 300 == 0)
                    {
                        scrollLeft();
                        populateRightEdge();
                    }
                    updateTitle();
                    msElapsed += 100;
                }
                
                //LEVEL FOUR
                else if(msElapsed<80000) 
                {
                    if(msElapsed == 60000)
                    {
                        mirror = true;
                        newLevel.play();
                        ImageIcon warning = new ImageIcon(Game.class.getResource("images/bunnyPortrait.png"));
                        Object[] options = { "Continue" };
                        JOptionPane.showOptionDialog(null,"What the heck is going on? Everything is moving right now!","Bunny",JOptionPane.YES_OPTION,JOptionPane.INFORMATION_MESSAGE, warning, options, null);
                        userImage = "images/userM.gif";
                        grid.setImage(new Location(userRow, userCol),userImage);
                    }
                    if(msElapsed % 1000 == 0&&isPoisoned)
                        poisoned();
                    if (msElapsed % 300 == 0)
                    {
                        scrollRight();
                        populateLeftEdge();
                    }
                    updateTitle();
                    msElapsed += 100;
                }
                
                //LEVEL FIVE
                else if(msElapsed<120000)
                {
                    if(msElapsed == 81000)
                    {
                        newLevel.play();
                        ImageIcon warning = new ImageIcon(Game.class.getResource("images/bunnyPortrait.png"));
                        Object[] options = { "Continue" };
                        JOptionPane.showOptionDialog(null,"Woah! What happened to my controls?\n...\nPull yourself together soldier!","Bunny",JOptionPane.YES_OPTION,JOptionPane.INFORMATION_MESSAGE, warning, options, null);
                    }
                    if(msElapsed % 1000 == 0&&isPoisoned)
                        poisoned();
                    if (msElapsed % 300 == 0)
                    {
                        scrollRight();
                        populateLeftEdge();
                    }
                    updateTitle();
                    msElapsed += 100;
                }
                //LEVEL SIX
                else if(msElapsed<160000)
                {
                    if(msElapsed == 120000)
                    {
                        newLevel.play();
                        ImageIcon warning = new ImageIcon(Game.class.getResource("images/bunnyPortrait.png"));
                        Object[] options = { "Continue" };
                        JOptionPane.showOptionDialog(null,"Great, my egg shooter is busted. It will take a while for me to fix it.","Bunny",JOptionPane.YES_OPTION,JOptionPane.INFORMATION_MESSAGE, warning, options, null);
                    }
                    if(msElapsed % 1000 == 0&&isPoisoned)
                        poisoned();
                    if(msElapsed % 300 == 0)
                        scrollRight();
                    if(msElapsed % 1200 == 0&&msElapsed<140000)
                        trapPattern();
                    if(msElapsed % 900 == 0&&msElapsed>=140000)
                        trapPattern();
                    updateTitle();
                    msElapsed += 100;
                }
                //LEVEL SEVEN
                else if(msElapsed<200000)
                {
                    if(msElapsed == 160000)
                    {
                        newLevel.play();
                        ImageIcon warning = new ImageIcon(Game.class.getResource("images/bunnyPortrait.png"));
                        Object[] options = { "Continue" };
                        JOptionPane.showOptionDialog(null,"Ok. Egg shooter fixed.","Bunny",JOptionPane.YES_OPTION,JOptionPane.INFORMATION_MESSAGE, warning, options, null);
                    }
                    if(msElapsed % 1000 == 0&&isPoisoned)
                        poisoned();
                    if(msElapsed % 300 == 0)
                        scrollRight();
                    if(msElapsed % 900 == 0)
                        trapPattern();
                    updateTitle();
                    msElapsed += 100;
                }
                //LEVEL EIGHT
                else if(msElapsed<220000)
                {
                    if(msElapsed == 200000)
                    {
                        mirror = false;
                        userImage = "images/user.gif";
                        grid.setImage(new Location(userRow,userCol),userImage);
                        newLevel.play();
                        ImageIcon warning = new ImageIcon(Game.class.getResource("images/bunnyPortrait.png"));
                        Object[] options = { "Continue" };
                        JOptionPane.showOptionDialog(null,"Alright, I regained my footing.\nLooks like the items are moving left as usual.","Bunny",JOptionPane.YES_OPTION,JOptionPane.INFORMATION_MESSAGE, warning, options, null);
                    }
                    if(msElapsed % 1000 == 0&&isPoisoned)
                        poisoned();
                    if(msElapsed % 300 ==0)
                        scrollLeft();
                    if(msElapsed % 900 == 0)
                        bossPattern();
                    updateTitle();
                    msElapsed += 100;
                }
                //LEVEL NINE
                else if(msElapsed<230000)
                {
                    if(msElapsed == 220000)
                    {
                        newLevel.play();
                    }
                    if(msElapsed % 1000 == 0&&isPoisoned)
                        poisoned();
                    if(msElapsed % 200 == 0)
                    {
                        scrollLeft();
                        populateRightEdge();
                    }
                    updateTitle();
                    msElapsed += 100;
                }
                //LEVEL TEN
                else
                {
                    if(msElapsed == 230000)
                    {
                        newLevel.play();
                    }
                    if(msElapsed % 1000 == 0&&isPoisoned)
                        poisoned();
                    if(msElapsed % 100 == 0)
                    {
                        scrollLeft();
                        populateRightEdge();
                    }
                    updateTitle();
                    msElapsed += 100;
                }
                
            }
                //DEFEAT
                currentAudio.stop();
                defeat();
       }
    }
    public void victory(){
        ImageIcon farmerSurrender=new ImageIcon(Game.class.getResource("images/farmerDialogBoxDefeated.png")); 
        Object[] options = { "Continue" };
        JOptionPane.showOptionDialog(null, "","Farmer",JOptionPane.YES_OPTION,JOptionPane.INFORMATION_MESSAGE, farmerSurrender, options, null);

        grid.setImage(new Location(farmerRow, farmerCol), "images/farmerDefeated.png");
        
        AudioClip winMusic = Applet.newAudioClip(Game.class.getResource("audio/winBGM.wav"));
        winMusic.loop();
        ImageIcon winIcon = new ImageIcon(Game.class.getResource("images/GameWon.png"));
        Object[] playAgain = {"Play Again?","No thanks, I've had enough carrots for one day"};
        int play = JOptionPane.showOptionDialog(null,"Time: "+msElapsed/1000+" seconds\nKill Streak: "+highestKillStreak+"\nTimes Hit: "+timesAvoid+"\nAmount of Carrots Collected: "+timesGet+"\nLife Remaining: "+remainingLife+"\nScore: "+getScore()+"\nRank: "+getRank(),"YOU WIN!",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE, winIcon, playAgain, null);
        if(play == 0)
        {
            winMusic.stop();
            Game game = new Game();
            game.play();
        }
        else if (play == 1)
        {
            winMusic.stop();
            ImageIcon portrait = new ImageIcon(Game.class.getResource("images/bunnyPortrait.png"));
            JOptionPane.showMessageDialog(null, "Well too bad! You can never have enough carrots!","NO!",JOptionPane.ERROR_MESSAGE,portrait);
            Game game = new Game();
            game.play();
        }
    }
    
    public void defeat()
    {
        AudioClip evilLaugh = Applet.newAudioClip(Game.class.getResource("audio/gameLostLaugh.wav"));
        AudioClip gameOverMusic = Applet.newAudioClip(Game.class.getResource("audio/defeatBGM.wav"));
        ImageIcon gameOverIcon = new ImageIcon(Game.class.getResource("images/GameOver.jpg"));
        evilLaugh.play();
        gameOverMusic.loop();
        Object[] playAgain = {"Play Again?","No thanks, I give up"};
        
        int play = JOptionPane.showOptionDialog(null,"Time: "+msElapsed/1000+" seconds\nKill Streak: "+highestKillStreak+"\nTimes Hit: "+timesAvoid+"\nAmount of Carrots Collected: "+timesGet+"\nScore: "+getScore()+"\nRank: "+getRank(),"YOU LOSE!",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE, gameOverIcon, playAgain, null);
        
        if(play == 0)
        {
            gameOverMusic.stop();
            Game game = new Game();
            game.play();
        }
        else if (play == 1)
        {
            gameOverMusic.stop();
            ImageIcon portrait = new ImageIcon(Game.class.getResource("images/bunnyPortrait.png"));
            JOptionPane.showMessageDialog(null, "Well too bad! A true bunny never gives up!","NO!",JOptionPane.ERROR_MESSAGE,portrait);
            Game game = new Game();
            game.play();
        }
    }
    public void handleKeyPress()
    {
        int key = grid.checkLastKeyPressed();
        //System.out.println(key);
        
        //up
        int upKey;
        if(gameMode.equals("survival")&&msElapsed>=80000&&msElapsed<200000)
            upKey = 40;
        else
            upKey = 38;
        
        if(key==upKey&&userRow!=0)
        {
            grid.setImage(new Location(userRow,userCol), "images/bg.jpg");
            userRow--;
            if(!grid.getImage(new Location(userRow,userCol)).equals("images/bg.jpg"))
                handleCollision(new Location(userRow,userCol));
            grid.setImage(new Location(userRow, userCol), userImage);

        }
        
        //down
        int downKey;
        if(gameMode.equals("survival")&&msElapsed>=80000&&msElapsed<200000)
            downKey = 38;
        else
            downKey = 40;
        
        if(key==downKey&&userRow!=grid.getNumRows()-1)
        {
            grid.setImage(new Location(userRow,userCol), "images/bg.jpg");
            userRow++;
            if(!grid.getImage(new Location(userRow,userCol)).equals("images/bg.jpg"))
                handleCollision(new Location(userRow,userCol));
            grid.setImage(new Location(userRow, userCol), userImage);
        }
      
        //right
        int rightKey;
        if(gameMode.equals("survival")&&msElapsed>=80000&&msElapsed<200000)
            rightKey = 37;
        else
            rightKey = 39;
        
        int forbiddenCol;
        if(mirror == true)
            forbiddenCol = grid.getNumCols()-1;
        else
            forbiddenCol = grid.getNumCols()-2;
        
        if(key==rightKey&&userCol<forbiddenCol)
        {
          grid.setImage(new Location(userRow,userCol),"images/bg.jpg");
          userCol++;
          if(!grid.getImage(new Location(userRow,userCol)).equals("images/bg.jpg"))
              handleCollision(new Location(userRow,userCol));
          grid.setImage(new Location (userRow,userCol),userImage);
        }
      
        //left
        int leftKey;
        if(gameMode.equals("survival")&&msElapsed>=80000&&msElapsed<200000)
            leftKey = 39;
        else
            leftKey = 37;
        
        int forbiddenCol2;
        if(mirror == true)
            forbiddenCol2 = 1;
        else
            forbiddenCol2 = 0;
        
        if(key==leftKey&&userCol>forbiddenCol2)
        {
            grid.setImage(new Location(userRow,userCol),"images/bg.jpg");
            userCol--;
            if(!grid.getImage(new Location(userRow,userCol)).equals("images/bg.jpg"))
                handleCollision(new Location(userRow,userCol));
            grid.setImage(new Location (userRow,userCol),userImage);
        }
        if((gameMode.equals("story")&&key == 83)||(gameMode.equals("survival")&&key == 83&&(msElapsed<120000||msElapsed>=160000)))
            shootEgg();
        
        //PAUSE MENU
        if(key == 80)
        {
            Object[] options = { "Return to Game","Settings" };
            int choice = JOptionPane.showOptionDialog(null,"","PAUSED",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null, options, null);
            if(choice == 1)
            {
                Object[] settingOptions = {"Egg Selection","Sound Control"};
                int settingChoice = JOptionPane.showOptionDialog(null,"","Settings",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE,null,settingOptions,null);
                if(settingChoice == 0)
                {
                    //EGG SELECTION
                    ImageIcon eggs = new ImageIcon(Game.class.getResource("images/eggSelect.jpg"));
                    Object[] eggOptions = {"Bee","Other Easter Egg","Carrot","Shark","Easter Egg","Tiger"};
                    int eggChoice = JOptionPane.showOptionDialog(null,"","EGG SELECTION",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE,eggs,eggOptions,null);
                    if(eggChoice == 0)
                        eggType = "bee";
                    else if(eggChoice == 1)
                        eggType = "purple";
                    else if(eggChoice == 2)
                        eggType = "carrot";
                    else if(eggChoice == 3)
                        eggType = "shark";
                    else if(eggChoice == 4)
                        eggType = "green";
                    else if(eggChoice == 5)
                        eggType = "tiger";
                }
                else if(settingChoice == 1)
                {
                    //VOLUME CONTROL
                    boolean notDone = true;
                    while(notDone)
                    {
                        Object[] volumeOptions = {"Mute","Unmute","Cancel"};
                        int volumeChoice = JOptionPane.showOptionDialog(null,"","Sound Control",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE,null,volumeOptions,null);
                        //Mute
                        if(volumeChoice == 0)
                        {
                            currentAudio.stop();
                        }
                        //Unmute
                        if(volumeChoice == 1)
                        {
                            currentAudio.loop();
                        }
                        //Cancel
                        if(volumeChoice == 2)
                        {
                            break;
                        }
                    }
                }
            }
        }
    }  
    
    public void populateRightEdge()
    {
        for(int r=0;r<grid.getNumRows();r++)
        {
            int randomNum=(int)(Math.random()*100);
            //System.out.println(randomNum);
            int lastCol=grid.getNumCols()-1;
            if(randomNum<=5&&msElapsed<230000)
              grid.setImage(new Location(r, lastCol), "images/get.png");
            
            //LEVEL ONE
            else if(randomNum>5&&randomNum<=10&&msElapsed < 20000)
              grid.setImage(new Location(r, lastCol), "images/carrotMonster.gif");

            //LEVEL TWO
            else if(randomNum>10&&randomNum<=20&&msElapsed>=20000)//More Monsters
              grid.setImage(new Location(r, lastCol), "images/carrotMonster.gif");
            else if(randomNum>20&&randomNum<=25&&msElapsed>=20000&&msElapsed<230000)
              grid.setImage(new Location(r, lastCol),"images/bearTrap.png");
                  
            //LEVEL THREE
            else if(randomNum>25&&randomNum<=35&&msElapsed>=40000&&!isPoisoned)
                grid.setImage(new Location(r,lastCol),"images/poisonCarrot.png");
            else if(randomNum>35&&randomNum<=36&&msElapsed>=40000&&isPoisoned)
                grid.setImage(new Location(r,lastCol),"images/goldenCarrot.png");
            
            //LEVEL TEN
            else if(randomNum>40&&randomNum<=50&&msElapsed>=230000)
                grid.setImage(new Location(r,lastCol),"images/bearTrap.png");
            
            else
              grid.setImage(new Location(r, lastCol), "images/bg.jpg");                 
        }
    }
    public void populateLeftEdge()
    {
        for(int r=0;r<grid.getNumRows();r++)
        {
            int randomNum=(int)(Math.random()*100);
            //System.out.println(randomNum);
            if(randomNum<=5)
              grid.setImage(new Location(r, 0), "images/get.png");
            else if(randomNum>10&&randomNum<=20&&msElapsed>=20000)//More Monsters
              grid.setImage(new Location(r, 0), "images/carrotMonsterM.gif");
            else if(randomNum>20&&randomNum<=25&&msElapsed>=20000)
              grid.setImage(new Location(r, 0),"images/bearTrap.png");
            else if(randomNum>25&&randomNum<=35&&msElapsed>=40000&&!isPoisoned)
                grid.setImage(new Location(r,0),"images/poisonCarrot.png");
            else if(randomNum>35&&randomNum<=36&&msElapsed>=40000&&isPoisoned)
                grid.setImage(new Location(r,0),"images/goldenCarrot.png");
            else
              grid.setImage(new Location(r, 0), "images/bg.jpg");                 
        }
    }


  public void scrollLeft()
  {
      for(int c=0;c<grid.getNumCols();c++)
      {
          for(int r=0;r<grid.getNumRows();r++)
          {
              Location loc=new Location(r,c);
              Location userLoc=new Location(userRow,userCol);

              //System.out.println(loc);
              if(!(grid.getImage(loc).equals("images/bg.jpg")||loc.equals(userLoc)||grid.getImage(loc).equals("images/farmer.png")))
              {
                  if(c==0)
                      grid.setImage(loc,"images/bg.jpg");
                  else
                  {
                      if(new Location(r,c-1).equals(userLoc))
                          handleCollision(loc);
                      grid.setImage(new Location(r,c-1), grid.getImage(loc));
                      grid.setImage(new Location(r,c), "images/bg.jpg");
                      grid.setImage(userLoc, userImage);
                  }
              }                            
          }
      }
  }
    public void scrollRight()
  {
      for(int c=grid.getNumCols()-1;c>=0;c--)
      {
          for(int r=grid.getNumRows()-1;r>=0;r--)
          {
              Location loc=new Location(r,c);
              Location userLoc=new Location(userRow,userCol);

              //System.out.println(loc);
              if(!(grid.getImage(loc).equals("images/bg.jpg")||loc.equals(userLoc)||grid.getImage(loc).equals("images/farmer.png")))
              {
                  if(c == grid.getNumCols()-1)
                      grid.setImage(loc,"images/bg.jpg");
                  else
                  {
                      if(new Location(r,c+1).equals(userLoc))
                          handleCollision(loc);
                      grid.setImage(new Location(r,c+1), grid.getImage(loc));
                      grid.setImage(loc, "images/bg.jpg");
                      grid.setImage(userLoc, userImage);
                  }
              }                            
          }
      }
  }
  
    public void handleCollision(Location loc)
    {     
        if(grid.getImage(loc).equals("images/get.png"))
        {
              grid.setImage(loc,"images/bg.jpg");
              AudioClip gotCarrot = Applet.newAudioClip(Game.class.getResource("audio/carrotGained.wav"));
              gotCarrot.play();

              timesGet++;
              countGet++;   
        }
        else if(grid.getImage(loc).equals("images/carrotMonster.gif")||grid.getImage(loc).equals("images/carrotMonsterM.gif"))
        {
              grid.setImage(loc,"images/bg.jpg");
              AudioClip monsterHit = Applet.newAudioClip(Game.class.getResource("audio/youch.wav"));
              monsterHit.play();
              if(killStreak > highestKillStreak)
                    highestKillStreak = killStreak;
              killStreak = 0;
              timesAvoid++;
              if(mirror == true)
                grid.setImage(new Location(userRow, userCol), "images/userHitM.png");
              else
                grid.setImage(new Location(userRow,userCol),"images/userHit.png");
              grid.pause(500);
              receiveHit = true;  
        }
        else if(grid.getImage(loc).equals("images/bearTrap.png"))
        {
            grid.setImage(loc,"images/bg.jpg");
            AudioClip trapSnap = Applet.newAudioClip(Game.class.getResource("audio/trapSnap.wav"));
            trapSnap.play();
            if(killStreak > highestKillStreak)
                    highestKillStreak = killStreak;
            killStreak = 0;
            if(mirror == true)
                grid.setImage(new Location(userRow,userCol),"images/userHitTrapM.png");
            else
                grid.setImage(new Location(userRow, userCol),"images/userHitTrap.png");
            grid.pause(500);
            hitTrap = true;  
        }
        else if(grid.getImage(loc).equals("images/poisonCarrot.png"))
        {
            grid.setImage(loc,"images/bg.jpg");
            AudioClip pCarrotGained = Applet.newAudioClip(Game.class.getResource("audio/pCarrotGained.wav"));
            pCarrotGained.play();
            if(mirror == true)
                grid.setImage(new Location(userRow,userCol),"images/userHitM.png");
            else
                grid.setImage(new Location(userRow,userCol),"images/userHit.png");
            Grid.pause(500);
            if(mirror == true)
                userImage = "images/userPoisonedM.gif";
            else
                userImage = "images/userPoisoned.gif";
            isPoisoned = true;
        }
        else if(grid.getImage(loc).equals("images/goldenCarrot.png"))
        {
            grid.setImage(loc,"images/bg.jpg");
            AudioClip goldenCarrot = Applet.newAudioClip(Game.class.getResource("audio/carrotGained.wav"));
            goldenCarrot.play();
            if(mirror == true)
                userImage = "images/userM.gif";
            else
                userImage = "images/user.gif";
            grid.setImage(new Location(userRow,userCol),userImage);
            isPoisoned = false;
        }
        //System.out.println("Times Get: "+timesGet);
        //System.out.println("Times Avoid: "+timesAvoid);
        grid.setImage(loc,"images/bg.jpg");
    }
    public int getScore()    // EACH "GET" = 10
    {
    return timesGet*10;
    }
  
    public void updateHP()  
    {
        // if 20 carrots are collected, user gains getLifeGained() HP
        if(countGet==20)
        {
          AudioClip oneUp = Applet.newAudioClip(Game.class.getResource("audio/lifeGained.wav"));
          oneUp.play();

          remainingLife+=getLifeGained();
          countGet = 0;
        }

        // if hit by carrot monster, user loses getMonsterDamage() HP
        if(receiveHit)
          remainingLife-=getMonsterDamage();
        receiveHit = false;

        //if hit by trap, user loses getTrapDamage() HP
        if(hitTrap)
          remainingLife-=getTrapDamage();
        hitTrap = false;
    }
  
    public void updateTitle()
    {
        updateHP();   
        grid.setTitle("Game: Carrot Liberation | Score: " + getScore()+" | HP: " + remainingLife);
    }
  
    public void shootEgg() 
    {
        AudioClip fireEgg = Applet.newAudioClip(Game.class.getResource("audio/explosion.wav"));
        fireEgg.play();
        if(mirror == true)
        {
            grid.setImage(new Location(userRow, userCol),"images/userShootM.gif");
            for(int c=userCol-1;c>=0;c--)
            {
                Location currentLoc=new Location(userRow,c);    
                if(grid.getImage(currentLoc).equals("images/bg.jpg"))
                {
                    //EGG APPEARANCE
                    if(eggType.equals("tiger"))
                        grid.setImage(currentLoc, "images/tigerEggM.png");
                    else if(eggType.equals("purple"))
                        grid.setImage(currentLoc, "images/purpleEggM.png");
                    else if(eggType.equals("green"))
                        grid.setImage(currentLoc, "images/greenEggM.png");
                    else if(eggType.equals("shark"))
                        grid.setImage(currentLoc, "images/sharkEggM.png");
                    else if(eggType.equals("carrot"))
                        grid.setImage(currentLoc, "images/carrotEggM.png");
                    else
                        grid.setImage(currentLoc, "images/beeEggM.png");  
                    grid.pause(50);
                }
            else if(grid.getImage(currentLoc).equals("images/carrotMonsterM.gif"))
            {
                AudioClip defeatMonster = Applet.newAudioClip(Game.class.getResource("audio/squish.wav"));
                defeatMonster.play();
                killStreak++;
                grid.setImage(currentLoc,"images/get.png");
                break;
            }
            else if(grid.getImage(currentLoc).equals("images/get.png")||grid.getImage(currentLoc).equals("images/poisonCarrot.png")||grid.getImage(currentLoc).equals("images/goldenCarrot.png"))
            {
                AudioClip defeatMonster = Applet.newAudioClip(Game.class.getResource("audio/squish.wav"));
                defeatMonster.play();

                grid.setImage(currentLoc,"images/bg.jpg");
                break;
            }
            if(!grid.getImage(currentLoc).equals("images/bearTrap.png"))
                grid.setImage(currentLoc,"images/bg.jpg");
            }
        }
        else
        {
            grid.setImage(new Location(userRow, userCol),"images/userShoot.gif");

            for(int c=userCol+1;c<grid.getNumCols();c++)
            {
                Location currentLoc=new Location(userRow,c);    
                if(grid.getImage(currentLoc).equals("images/bg.jpg"))
                {
                    //EGG APPEARANCE
                    if(eggType.equals("tiger"))
                        grid.setImage(currentLoc, "images/tigerEgg.png");
                    else if(eggType.equals("purple"))
                        grid.setImage(currentLoc, "images/purpleEgg.png");
                    else if(eggType.equals("green"))
                        grid.setImage(currentLoc, "images/greenEgg.png");
                    else if(eggType.equals("shark"))
                        grid.setImage(currentLoc, "images/sharkEgg.png");
                    else if(eggType.equals("carrot"))
                        grid.setImage(currentLoc, "images/carrotEgg.png");
                    else
                        grid.setImage(currentLoc, "images/beeEgg.png");  

                    grid.pause(50);
                }
                else if(grid.getImage(currentLoc).equals("images/carrotMonster.gif"))
                {
                    AudioClip defeatMonster = Applet.newAudioClip(Game.class.getResource("audio/squish.wav"));
                    defeatMonster.play();
                    killStreak++;
                    grid.setImage(currentLoc,"images/get.png");
                    break;
                }
                else if(grid.getImage(currentLoc).equals("images/get.png")||grid.getImage(currentLoc).equals("images/poisonCarrot.png")||grid.getImage(currentLoc).equals("images/goldenCarrot.png"))
                {
                    AudioClip defeatMonster = Applet.newAudioClip(Game.class.getResource("audio/squish.wav"));
                    defeatMonster.play();

                    grid.setImage(currentLoc,"images/bg.jpg");
                    break;
                }
                else if(grid.getImage(currentLoc).equals("images/farmer.png"))
                {
                    timesHitFarmer++;
                    break;
                }
                if(!grid.getImage(currentLoc).equals("images/bearTrap.png"))
                    grid.setImage(currentLoc,"images/bg.jpg");
            }
        }
}
  
    public void moveFarmer()  
    {
        if(msElapsed%600==0)
        {
            double random=Math.random();
            if(random<.5&&farmerRow!=0)
            {
              grid.setImage(new Location(farmerRow, farmerCol),"images/bg.jpg");
              farmerRow--;
              grid.setImage(new Location(farmerRow, farmerCol), "images/farmer.png");
            }
            else if(random>=.5&&farmerRow!=grid.getNumRows()-1)
            {
              grid.setImage(new Location(farmerRow, farmerCol),"images/bg.jpg");
              farmerRow++;
              grid.setImage(new Location(farmerRow, farmerCol), "images/farmer.png");
            }
        }
    }
        
    public void throwFork()
    {
        if(userRow == farmerRow)
        {
            grid.setImage(new Location(farmerRow, farmerCol),"images/farmerThrowing.gif");
            for(int c=farmerCol;c>=0;c--)
            { 
                Location currentLoc=new Location(farmerRow,c); 
                if(grid.getImage(currentLoc).equals("images/bg.jpg"))
                { 
                    grid.setImage(currentLoc, "images/movingPitchFork.png");
                    grid.pause(30);
                    grid.setImage(currentLoc,"images/bg.jpg");
                }
                else if(grid.getImage(currentLoc).equals(userImage)||grid.getImage(currentLoc).equals("images/userShoot.gif"))
                {
                    grid.setImage(currentLoc,"images/userHit.png");
                    if(killStreak > highestKillStreak)
                        highestKillStreak = killStreak;
                    killStreak = 0;
                    grid.pause(300);
                    remainingLife-=getForkDamage();
                    break;
                }
            }
        }
    }
    
    public void bossPattern()
    {
        int altRow = 0;
        for(int r = 0; r < grid.getNumRows(); r++)
        {
            int lastCol = grid.getNumCols() - 2;
            if(altPattern == 0)
            {
                if(altRow == 0)
                { 
                    grid.setImage(new Location(r, lastCol),"images/bearTrap.png");
                    altRow = 1;
                }
                else if(altRow == 1)
                {
                    grid.setImage(new Location(r, lastCol), "images/carrotMonster.gif");
                    altRow = 0;
                }
                if(r == grid.getNumRows()-1)
                {
                    altPattern = 1;
                }
            }
            else if(altPattern == 1)
            {
                if(altRow == 0)
                {
                    grid.setImage(new Location(r, lastCol), "images/carrotMonster.gif");
                    altRow = 1;
                }
                else if(altRow == 1)
                {
                    grid.setImage(new Location(r, lastCol),"images/bearTrap.png");
                    altRow = 0;
                }
                if(r == grid.getNumRows()-1)
                {
                    altPattern = 0;
                }
            }
            if(new Location(userRow,userCol).equals(new Location(r,lastCol)))
                        handleCollision(new Location(r,lastCol));
        }
        
    }
    public void trapPattern()
    {
        int random = (int)(Math.random()*5);
        //LEVEL SIX
        if(msElapsed<160000)
        {
            for(int r=0;r<grid.getNumRows();r++)
            {
                if(r == random)
                {
                    if(isPoisoned)
                        grid.setImage(new Location(r,0),"images/goldenCarrot.png");
                    else
                        grid.setImage(new Location(r,0),"images/bg.jpg");
                }
                else
                    grid.setImage(new Location(r,0),"images/bearTrap.png");
            }
        }
        //LEVEL SEVEN
        else
        {
            for(int r=0;r<grid.getNumRows();r++)
            {
                if(r == random)
                    grid.setImage(new Location(r,0),"images/carrotMonsterM.gif");
                else
                    grid.setImage(new Location(r,0),"images/bearTrap.png");
            }
        }
    }
   
    public String getRank()
    {
        String rank;
        int score = getScore();
        if(score < 500)
            rank = "Private";
        else if(score < 600)
            rank = "Corporal";
        else if(score < 700)
            rank = "Sergeant";
        else if(score < 800)
            rank = "Lieutenant";
        else if(score < 900)
            rank = "Captain";
        else if(score < 1000)
            rank = "Major";
        else if(score < 1100)
            rank = "Colonel";
        else if(score < 1200)
            rank = "General";
        else
            rank = "Carrot Liberator";
        return rank;
    }

    public boolean isGameOver()
    {
        return remainingLife<=0||timesHitFarmer == getFarmerHP();
    }

    public void poisoned()
    {
        if(mirror == true)
            grid.setImage(new Location(userRow, userCol),"images/userHitM.png");
        else
            grid.setImage(new Location(userRow, userCol),"images/userHit.png");
        grid.pause(50);
        if(remainingLife<=getPoisonDamage())
            remainingLife = 1;
        else
            remainingLife-=getPoisonDamage();
    }
    
    //Calculates damage when hit by carrot monster
    public int getMonsterDamage()
    {
        if(difficulty.equals("easy"))
            return 50;
        else if(difficulty.equals("normal"))
            return 150;
        else
            return 250;
    }
    
    //Calculates damage when hit by bear trap
    public int getTrapDamage()
    {
        if(difficulty.equals("easy"))
            return 100;
        else if(difficulty.equals("normal"))
            return 200;
        else
            return 300;
    }
    
    //Calculates damage when hit by farmer's pitch fork
    public int getForkDamage()
    {
        if(difficulty.equals("easy"))
            return 250;
        else if(difficulty.equals("normal"))
            return 350;
        else
            return 450;        
    }
    
    //Calculates damage when poisoned
    public int getPoisonDamage()
    {
        if(difficulty.equals("easy"))
            return 15;
        else if(difficulty.equals("normal"))
            return 30;
        else
            return 60;
    }
    
    //Calculates life gained when obtaining 20 carrots
    public int getLifeGained()
    {
        if(difficulty.equals("easy"))
            return 500;
        else if(difficulty.equals("normal"))
            return 300;
        else
            return 100;
    }
    
    //Calculates amount of hits for farmer to win the game
    public int getFarmerHP()
    {
        if(difficulty.equals("easy"))
            return 15;
        else if(difficulty.equals("normal"))
            return 20;
        else
            return 25;
    }
    
    public static void test()
    {
        Game game = new Game();
        game.play();
    }

    public static void main(String[] args)
    {
        Game.test();
    }
    public void paint(Graphics g){
        super.paint(g);
        g.drawString("test",25,25);
    }
}