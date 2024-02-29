package gamelogic;

import java.awt.Component;

import javax.swing.JOptionPane;

public class jogo {
	Component jFrame;
	public hand player, dealer, split;
	public int gameover, state;
	
	public jogo() {
		this.jFrame = null;
		this.player = new hand(); 
		this.dealer = new hand(); 
		this.split = new hand();
		this.gameover=-1;
		this.state = -1;
    }
	
	
	public void print() {//NOT USED IN GUI
		System.out.println("--------------");
		System.out.println(player.num);
  	   	System.out.println(dealer.num);
  	   	System.out.println("--------------");
	}
	
	
	public int isGameOver() {
		if(player.num>21) {
			JOptionPane.showMessageDialog(jFrame, "Bust! You lose.");
			System.out.println("Bust");
			gameover=2;//LOSS
			player.stand();
		}else if(player.num==21 && dealer.stand && dealer.num!=21) {
			JOptionPane.showMessageDialog(jFrame, "Blackjack! You win.");
			System.out.println("Blackjack");
			gameover=1;//WIN
		}else if(dealer.num>21) {
			JOptionPane.showMessageDialog(jFrame, "Dealer Bust! You win.");
			System.out.println("Dealer Bust");
			gameover=1;
		}else if(dealer.stand && player.num>dealer.num) {
			JOptionPane.showMessageDialog(jFrame, "Player has higher total! You win.");
			System.out.println("Player has higher total");
			gameover=1;
		}else if(player.stand && player.num<dealer.num) {
			JOptionPane.showMessageDialog(jFrame, "Dealer has higher total! You lose.");
			System.out.println("Dealer has higher total");
			gameover=2;
		}else if(player.stand && dealer.stand && player.num==dealer.num) {
			JOptionPane.showMessageDialog(jFrame, "Tie");
			System.out.println("Tie");
			gameover=3;//TIE
		}
		return gameover;
	}
	
	public int splitOver() {
		if(split.num>21) {
			JOptionPane.showMessageDialog(jFrame, "split: Bust! You lose.");
			System.out.println("Bust");
			state=2;
			split.stand();
		}else if(split.num==21 && dealer.stand && dealer.num<21) {
			JOptionPane.showMessageDialog(jFrame, "split: Blackjack! You win.");
			System.out.println("Blackjack");
			state=1;
		}else if(dealer.num>21) {
			JOptionPane.showMessageDialog(jFrame, "split: Dealer Bust! You win.");
			System.out.println("Dealer Bust");
			state=1;
		}else if(dealer.stand && split.num>dealer.num) {
			JOptionPane.showMessageDialog(jFrame, "split: Player has higher total! You win.");
			System.out.println("Player has higher total");
			state=1;
		}else if(split.stand && split.num<dealer.num) {
			JOptionPane.showMessageDialog(jFrame, "split: Dealer has higher total! You lose.");
			System.out.println("Dealer has higher total");
			state=2;
		}else if(split.stand && dealer.stand && split.num==dealer.num) {
			JOptionPane.showMessageDialog(jFrame, "split: Tie");
			System.out.println("Tie");
			state=3;
		}
		return state;
	}
	
	public void newGame() {
		gameover = 0;
		state = -1;
		player.setNewGame();  
		dealer.setNewGame();
		split.setNewGame();
		//Initial cards
		player.hit();
		player.hit();
		dealer.hit();
		System.out.println("New game starting!");
	}
	
	public void dealer() {
		do {
			if(dealer.num<17) {
				dealer.hit();
			}
			if(dealer.num>16) {
				dealer.stand();
			}
		}while(!dealer.stand);
	}
	
	public void split() {
		player.cards[1]=0;
		player.figure[1]=0;
		player.suit[1]=0;
		split.cards[0]=player.cards[0];
		if(split.cards[0]==1) {
			split.ace++;
			player.ace--;
		}
		split.figure[0]=player.figure[0];
		split.suit[0]=player.suit[0];
		player.setNum();
		split.setNum();
	}
	
	
}