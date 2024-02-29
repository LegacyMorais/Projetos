package gamelogic;
import java.util.Random;

public class hand {
	public int num;//Numero total da soma das cartas
	public int ace;//Numero de ases
	public int[] figure;//Figura para reis/valetes,etc
	public int[] suit;//Cor das cartas
	public int[] cards;//Numero das cartas com ás a contar como 1
	public boolean stand;//Stand
	private static int N=7;
	
	public hand() {
		this.num = 0;
		this.ace = 0;
		this.cards = new int[N];
		this.suit = new int[N];
		this.figure = new int[N];
		for(int i=0; i<N; i++) {
			this.cards[i] = 0;  
		}
		for(int i=0; i<N; i++) {
			this.figure[i] = 0;  
		}
		for(int i=0; i<N; i++) {
			this.suit[i] = 0;  
		}
		this.stand = false;
	}
	
	public void setNewGame() {
		num = 0;
		ace = 0;
		stand = false;
		for(int i=0; i<N; i++) {
			cards[i] = 0;  
		}
		for(int i=0; i<N; i++) {
			this.figure[i] = 0;  
		}
		for(int i=0; i<N; i++) {
			this.suit[i] = 0;  
		}
		setNum();
	}
	
	public void hit() {
	      Random rand = new Random(); //instance of random class
	      int upperbound = 13; 
	      int int_random = rand.nextInt(upperbound);
	      //Generates numbers from 0-12
	     
	      
	      for(int i=0; i<N; i++) {
	    	  if(cards[i]==0) {
	    		  if(int_random > 8){//FIGURAS
	    			  figure[i] = int_random + 1;
	    	          int_random = 9;
	    	      }
	    		
	    		  cards[i] = int_random + 1;   //adds random values from 1-10   
	    		
	    		  //Increment the ace counter
	   	      	  if(int_random == 0){
	   	      			ace++;
	   	      	  }
	   	      	  
	   	      	  //Set Suit
	   		      upperbound = 4; 
	   		      int_random = rand.nextInt(upperbound);
	   	      	  suit[i] = int_random + 1;
	   	      	  
	    		  i = N;
	    	  }
			}
	      
	      //Set Aces
	     setNum();	     
	}
	
	public void stand() {
	      stand = true;
	}
	
	public int sum() {
		int r=0;
		for(int i=0; i<N; i++) {
			r = r + cards[i];
		}
		return r;
	}
	
	public void setNum() {
		int i;
		for(i=ace; i>0; i--) {
			if(sum()+(i*10) < 22 ) {
				num = sum()+(i*10);
				i = -1;
			} 
		}
		if(i==0) {
			num = sum();
		}
	}
	
}

