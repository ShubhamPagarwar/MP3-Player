/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
 import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import java.awt.Component;
import java.awt.*;
import java.io.*;
import java.sql.*;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.*;
import javax.swing.*;
import java.util.*;
import javax.swing.event.*;
import java.awt.event.*;
import custom_pkg.*;
import javax.swing.filechooser.*;
import javax.swing.KeyStroke;


public class Myproject1  implements ActionListener,ChangeListener,Runnable,ListSelectionListener, ItemListener
{
    JFrame window;
    JPanel toppanel,midpanel,centrepanel,playlistpanel,buttonPanel1;
	Choice choice;
	DefaultListModel listmodel;
	JScrollPane jscroll;
	JList lst;
    JSlider s;
    JButton b,pause,resume,stop,addP,removeP,saveP;
    JFileChooser fc;
    JMenuBar m;
    JMenu file,visual,help;
    JMenuItem open,newPlaylist,d1,d2,ball,heart,about;
    static String name;
    File tf,of;
    JLabel l1,l3,l4,tl;
    JLabel l[];   
    InputStream fi;
	 Thread t;
    Thread t1=new Thread(this);
    ImageIcon i1;
    int NOTSTARTED = 0, PLAYING = 1,PAUSED = 2,FINISHED = 3,PLAYING2=0;
    Player player;
    // locking object used to communicate with player thread
    Object playerLock = new Object();
	// status variable what player thread is doing/supposed to do
	int playerStatus = NOTSTARTED;
	ListName listname;
	PlayList playlist;
	Runnable r;

	public Myproject1()
	{
		window=new JFrame("MP3 Player");
		//window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		m=new JMenuBar();
		file=new JMenu("file");
		visual=new JMenu("Visualizer");
		help=new JMenu("Help");
		open=new JMenuItem("Open");
		file.addSeparator();
		newPlaylist=new JMenuItem("New Playlist");
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,CTRL_DOWN_MASK));
		//open.setMnemonic('o');
		d1=new JMenuItem("Design1");
		ball=new JMenuItem("ball");
		heart=new JMenuItem("Heart");
		about=new JMenuItem("About");
		d2=new JMenuItem("Design2");
		name="Ashq.mp3";
		
		centrepanel=new JPanel();
		midpanel=new JPanel();
		toppanel=new JPanel();
        playlistpanel=new JPanel();
		buttonPanel1=new JPanel();
		listmodel=new DefaultListModel();
		lst=new JList(listmodel);
			lst.addListSelectionListener(this);
		jscroll=new JScrollPane(lst);
		
		choice=new Choice();
		choice.addItemListener(this);
		buttonPanel1.setLayout(new FlowLayout(FlowLayout.CENTER));
		playlistpanel.setLayout(new BorderLayout());
        centrepanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		toppanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		midpanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		playlistpanel.add(choice, BorderLayout.NORTH);
		playlistpanel.add(jscroll,BorderLayout.CENTER);
		l3=new JLabel("");
		l1=new JLabel("Dazzler MP3 Player");
		l1.setFont(new Font("Algerian",Font.BOLD+Font.ITALIC,30));
		l=new JLabel[7];
		for(int i=0;i<7;i++)
			l[i]=new JLabel();
		tl=new JLabel();
		l4=new JLabel("Volume");
		b=new JButton(new ImageIcon("Play.png"));
		s=new JSlider(JSlider.VERTICAL, 0,100,10);
		s.setMajorTickSpacing(50);
		s.setPaintTicks(true);
		pause= new JButton("Pause");
		resume= new JButton("Resume");
		stop= new JButton("stop");
		addP=new JButton("ADD");
		removeP=new JButton("REMOVE");
		//saveP=new JButton("SAVE");
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("mp3 Files only", "mp3");
		fc.addChoosableFileFilter(filter);
		open.addActionListener(this);
		newPlaylist.addActionListener(this);
		d1.addActionListener(this);
		d2.addActionListener(this);
		ball.addActionListener(this);
		heart.addActionListener(this);
		b.addActionListener(this);
		pause.addActionListener(this);
		resume.addActionListener(this);
		stop.addActionListener(this);
		addP.addActionListener(this);
		removeP.addActionListener(this);
		//saveP.addActionListener(this);
		about.addActionListener(this);
		s.addChangeListener(this);
			
		toppanel.add(l1);
       
        
		centrepanel.add(b);
        
		file.add(open);
		//file.addSeparator();
		file.add(newPlaylist);
		visual.add(d1);
		visual.add(d2);
		visual.add(ball);
		visual.add(heart);
		help.add(about);
		m.add(file);
		m.add(visual);
		m.add(help);
		centrepanel.add(pause);
		centrepanel.add(resume);
		centrepanel.add(stop);
		centrepanel.add(l3);
		//centrepanel.add(s);
		centrepanel.add(l4);
		//playlistpanel.add(s,BorderLayout.WEST);
		buttonPanel1.add(addP);
		buttonPanel1.add(removeP);
		//buttonPanel1.add(saveP);
		playlistpanel.add(buttonPanel1,BorderLayout.SOUTH);
		Dimension d=new Dimension(200,400);
        playlistpanel.setPreferredSize(d);
		for(int i=0;i<7;i++)
			midpanel.add(l[i]);
		midpanel.add(tl);
		midpanel.setBackground(Color.black);
		window.add(centrepanel, BorderLayout.SOUTH);
		window.add(playlistpanel,BorderLayout.EAST);
		window.add(toppanel, BorderLayout.NORTH);
		window.add(midpanel, BorderLayout.CENTER);
		window.setJMenuBar(m);
		window.setBackground(new Color(150,140,150));
		window.setSize(460,500);
		window.setLocationRelativeTo(null);
		window.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent ee)
			{
				listname.close();
				System.exit(0);
			}
		});

		window.setVisible(true);
		listname=new ListName();
		showlist();
		t1.start();
	}
	
	public void run()
	{
		try
		{
			while(true)
			{
				l1.setForeground(Color.RED);
				t1.sleep(500);
				l1.setForeground(Color.BLUE);
				t1.sleep(500);
				l1.setForeground(Color.YELLOW);
				t1.sleep(500);
				l1.setForeground(Color.GREEN);
				t1.sleep(500);
				l1.setForeground(Color.CYAN);
				t1.sleep(500);
				l1.setForeground(Color.MAGENTA);
				t1.sleep(500);
			}
		}catch(Exception e){}
	}
	public void initi(InputStream fis)throws JavaLayerException
	{
        player = new Player(fis);
		l3.setText(name);
		window.setTitle(name);
		
    }
    
    public void stateChanged(ChangeEvent e)
    {
       
        l4.setText(""+s.getValue()+" %");
          
/*       s = (JSlider) e.getSource();
if(player!=null){
s.getMinimum();
long durationNanoseconds =
(player.getPosition());
s.setMaximum((int) player.getPosition());
int duration=(int) player.getPosition();
int percent = s.getValue();
long t = (durationNanoseconds / duration) * percent;
Time newTime = new Time(t);
s.setMajorTickSpacing(30);
pause();
player.wait(newTime);
player.start();
*/
	}
	public void itemStateChanged(ItemEvent e)
	{
		showSelectedList();
		
	}
	void showSelectedList()
	{
		//listname.allname();
		String s=choice.getSelectedItem();
		playlist=listname.getlist(s);
		System.out.println("showlist"+s);
		showlist();
	}
	void newPlaylist()
	{
		String str1=Util.sInput("Enter Name of New PlayList");
		playlist=new PlayList(str1);
		choice.add(str1);
		fc.showOpenDialog(window);
		File f[]=fc.getSelectedFiles();
		for(int i=0; i<f.length;i++)
		{
			str1=""+f[i];
			System.out.println(str1);
			playlist.addlist(str1);
		}
		System.out.println("asd");
		listname.addList(playlist);
		System.out.println("asd2");
		showlist();
		System.out.println("asd3");
	}
    void removePlaylist()
	{
		String s=choice.getSelectedItem();
		int t=JOptionPane.showConfirmDialog(null,"do you want to remove playlist = "+s,"choose one", JOptionPane.YES_NO_OPTION);
		System.out.println(t);
		if(t==0)
		{
			choice.remove(s);
			listname.removePlaylist(s);
			listname.allname();
			showSelectedList();
		}
		playerStatus =FINISHED;
		
		
	}
	
    public void play() throws JavaLayerException 
	{
            
          synchronized (playerLock) 
		{
			r = new Runnable()
			{
				public void run() 
				{
					 playInternal();
				}
            };
            t = new Thread(r);
            //t.setPriority(Thread.MAX_PRIORITY);
            playerStatus = PLAYING;
            t.start();
			}    
       // }
    }
         
    /**
     * Pauses playback. Returns true if new state is PAUSED.
     */
    public boolean pause()
	{
              
        synchronized (playerLock) 
		{
            if (playerStatus == PLAYING) 
			{
                playerStatus = PAUSED;
            }
            return playerStatus == PAUSED;
        }
        
    }

    /**
     * Resumes playback. Returns true if the new state is PLAYING.
     */
    public boolean resume() 
	{
        synchronized (playerLock) 
		{
			if (playerStatus == PAUSED) 
			{
				playerStatus = PLAYING;
                playerLock.notifyAll();
            }
            return playerStatus == PLAYING;
        }
    }


    private void playInternal() 
	{
            
           while (playerStatus != FINISHED) 
		{
			try 
			{
                                 if (!player.play(1)) 
				    break;
                
                        }catch (final JavaLayerException e) 
			{
                          break;
                        }
                        synchronized (playerLock) 
			{
                              while (playerStatus == PAUSED) 
				{
                                 try{
                                     playerLock.wait();
                                     } catch (final InterruptedException e) 
					{
                                        break;
                                      }
                                 }
                         }
                     }
                   b.setIcon(new ImageIcon("Play.png"));
                try
	           {
			fi=new FileInputStream(name);
		        initi(fi);
		   }
	 	    catch(Exception e1){e1.printStackTrace();}
        
                  }

    /**
     * Closes the player, regardless of current state.
     */
    public void stop() 
	{
        synchronized (playerLock) 
		{
            playerStatus = FINISHED;
            playerLock.notifyAll();
        }
        try 
		{
            player.close();
        } catch (final Exception e) 
		{
            // ignore, we are terminating anyway
        }
    }
	void showlist()
	{
		listmodel.clear();
		int i=0,n=0;
		try
		{
			n=playlist.sizeOf();
		}
		catch(Exception ee)
		{}
		
		System.out.println(n);
		for(i=0;i<n;i++)
			listmodel.addElement(playlist.getFileName(i));
	}
	public void valueChanged(ListSelectionEvent ei)
	{
		name=(String)lst.getSelectedValue();
		try
		{
			fi=new FileInputStream(name);
			initi(fi);
		}	
		catch(Exception e1)
		{
			System.out.println("enidgnidrnfoind\n");
			e1.printStackTrace();
		}	 
	}
    public static void main(String[] args) 
	{
		try
		{   
            FileInputStream fis=new FileInputStream("Ashq.mp3"); 
            Myproject1 m=new Myproject1();                    
            m.initi(fis);
        } catch (final Exception e) 
		{
            throw new RuntimeException(e);
        }
	}
    public void actionPerformed(ActionEvent e)
    {  
		try
		{
			if(e.getSource()==b)
                 {
                        if(playerStatus==NOTSTARTED)
                       {
                               b.setIcon(new ImageIcon("Pause.png"));
                             play();
			}
                        else if(playerStatus==PLAYING)
                       {
                               b.setIcon(new ImageIcon("Play.png"));
                             pause();
			}                               
                       else if(playerStatus==PAUSED)
                        {
                           b.setIcon(new ImageIcon("Pause.png"));
                           resume();
                        }
                       else if(playerStatus==FINISHED)
                        {
                               b.setIcon(new ImageIcon("Pause.png"));
                             play();
                         }
                       
                 }                               
			else if(e.getSource()==pause)
				pause();
			else if(e.getSource()==resume)
				resume();
			else if(e.getSource()==stop)
				stop();
			else if (e.getSource()==open)
			{
						//fc.setMultiSelectionEnabled(true);
				int r = fc.showOpenDialog(window);
				
				tf = fc.getSelectedFile(); // File type
				if (r == JFileChooser.CANCEL_OPTION)
					JOptionPane.showMessageDialog(window, "File NotSelected","Error", JOptionPane.ERROR_MESSAGE);
				else 
				{
					name = tf.getPath();
					if (!(name.endsWith(".mp3")))
						JOptionPane.showMessageDialog(window, "Select OnlyMP3","Error", JOptionPane.ERROR_MESSAGE);
					else 
					{
						
						try
						{
							fi=new FileInputStream(name);
							initi(fi);
						}
						catch(Exception e1){e1.printStackTrace();}
					}
				}
                              
			}
			else if(e.getSource()==removeP)
			{
				removePlaylist();
			}
			else if(e.getSource()==addP)
			{
				newPlaylist();
			}
			else if(e.getSource()==addP)
			{
				listname.close();
			}
			else if (e.getSource()==d1)
			{ 
				i1=new ImageIcon("design1.gif");
				tl.setIcon(new ImageIcon("black.png"));            
				for(int i=0;i<7;i++)
				{
					l[i].setIcon(i1);
				} 
			}   
			else if (e.getSource()==heart)
			{
				i1=new ImageIcon("Heart.gif");
             l[0].setIcon(i1);
             l[1].setIcon(i1);
             l[2].setIcon(i1);  
        }   
		else if (e.getSource()==ball)
        {
			i1=new ImageIcon("ball1.gif");
			l[0].setIcon(i1);
            l[1].setIcon(new ImageIcon("ball2.gif"));
            l[2].setIcon(new ImageIcon("ball1.gif"));
            l[3].setIcon(new ImageIcon("ball2.gif"));
            l[4].setIcon(new ImageIcon("ball1.gif"));
            l[5].setIcon(new ImageIcon("ball2.gif"));
            l[6].setIcon(new ImageIcon("ball1.gif"));
            tl.setIcon(new ImageIcon("line1.gif"));
        }   
		else if (e.getSource()==d2)
        {
			i1=new ImageIcon("design2.gif");
			l[0].setIcon(i1);
            l[1].setIcon(i1);
            l[2].setIcon(i1);
            
        }   
		else if (e.getSource()==about)
        {
			JOptionPane.showMessageDialog( window,"Created By: Shubham Pagarwar, Vinay Mahamuni","Owner", JOptionPane.INFORMATION_MESSAGE);
        }                  
       
    }catch(JavaLayerException ex){}  
     
   }
     
}
class PlayList
{
	public String str;
	public LinkedList linklist;
	PlayList(String s)
	{
		str=s;
		linklist=new LinkedList();
	}
	void addlist(String s)
	{
		int i=0,n=linklist.size();
		for(i=0;i<n;i++)
		{
			if(s.equals((String)linklist.get(i)))
				break;
		}
		if(i==n)
			linklist.add(s);
	}
	
	String getFileName(int i)
	{
		String str=(String)linklist.get(i);
		return(str);
	}
	
	int sizeOf()
	{
		int i=linklist.size();
		return(i);
	}

}

class ListName
{
	public LinkedList listnm;
	ListName()
	{
		listnm=new LinkedList();
		open();
	}
	void open()
	{
		ObjectInputStream ois=null;
		FileInputStream fis1=null;
		try
		{
			fis1=new FileInputStream("Playlists.dat");
			ois=new ObjectInputStream(fis1);
			listnm=(LinkedList)ois.readObject();
			ois.close();
			fis1.close();
			
		}
		catch(Exception e)
		{}
		
	}
	void close()
	{
		ObjectOutputStream oos=null;
		FileOutputStream fos1=null;
		try
		{
			fos1=new FileOutputStream("Playlists.dat");
			oos=new ObjectOutputStream(fos1);
			oos.writeObject(listnm);
			oos.close();
			fos1.close();
		}
		catch(Exception e)
		{}
		
	}
	void addList(PlayList p)
	{
		listnm.add(p);
		close();
	}
	PlayList getlist(String s)
	{
		PlayList p=null;
		System.out.println("incoming name="+s+"size of list="+listnm.size());
		for(int i=0;i<listnm.size();i++)
		{
			p=(PlayList)listnm.get(i);
			System.out.println(p.str);
			if(s.equals(p.str))
				break;
		}
		return(p);
	}
	 void allname()
	{
		PlayList p=null;
		for(int i=0;i<listnm.size();i++)
		{
			p=(PlayList)listnm.get(i);
			System.out.println(p.str);
		}
	
	} 
	void removePlaylist(String s)
	{
		PlayList p=getlist(s);
		listnm.remove(p);
	}
}

