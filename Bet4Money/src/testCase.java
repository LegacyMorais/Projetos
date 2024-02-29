import static org.junit.Assert.*;

import bet4money.*;
import gamelogic.*;

import org.junit.Test;

public class testCase {

	@Test
	public void testGetString() {
		data data = new data();
		game game = new game(data);
		assertEquals("/img/2_of_spades.png" ,game.getstring(2, 1, 0));
	}
	
	@Test
	public void testNewHand() {
		hand hand = new hand();
		hand.hit();
		hand.setNewGame();
		assertEquals(hand.num,0);
	}
	
	@Test
	public void testHit() {
		hand hand = new hand();
		hand.hit();
		if(hand.num>0) {
			assertEquals(1,1);
		}else {
			fail("Hello");
		}
	}
	
	@Test
	public void testStand() {
		hand hand = new hand();
		hand.stand();
		if(hand.stand) {
			assertEquals(1,1);
		}else {
			fail("Hello");
		}
	}
	
	@Test
	public void testSum() {
		hand hand = new hand();
		hand.cards[0] = 5;
		hand.cards[1] = 6;
		hand.cards[2] = 2;
		assertEquals(hand.sum(),13);	
	}
	
	@Test
	public void testSetNum() {
		hand hand = new hand();
		hand.cards[0] = 5;
		hand.cards[1] = 6;
		hand.cards[2] = 2;
		hand.setNum();
		assertEquals(hand.num,13);
	}
	
	@Test
	public void testSetNumAS() {
		hand hand = new hand();
		hand.cards[0] = 5;
		hand.cards[1] = 2;
		hand.cards[2] = 1;
		hand.ace = 1;
		hand.setNum();
		assertEquals(hand.num,18);
	}
	
	@Test
	public void testGameOver1() {
		jogo jogo = new jogo();
		jogo.player.num = 32;		
		assertEquals(jogo.isGameOver(),2);
	}
	
	@Test
	public void testGameOver2() {
		jogo jogo = new jogo();
		jogo.player.num = 20;
		jogo.dealer.num = 19;
		jogo.dealer.stand = true;
		assertEquals(jogo.isGameOver(),1);
	}
	
	@Test
	public void testSplitOver1() {
		jogo jogo = new jogo();
		jogo.split.num = 32;		
		assertEquals(jogo.splitOver(),2);
	}
	
	@Test
	public void testSplitOver2() {
		jogo jogo = new jogo();
		jogo.split.num = 20;
		jogo.dealer.num = 20;
		jogo.dealer.stand = true;
		jogo.split.stand = true;
		assertEquals(jogo.splitOver(),3);
	}
	
	@Test
	public void testNewGame() {
		jogo jogo = new jogo();
		jogo.newGame();
		if(jogo.player.cards[0]>0 && jogo.player.cards[1]>0 && jogo.player.cards[3]==0 && jogo.dealer.cards[0]>0 && jogo.dealer.cards[1]==0 && jogo.state == -1 && jogo.gameover==0) {
			assertEquals(1,1);
		}else {
			fail("Hello");
		}
	}
	
	@Test
	public void testDealer() {
		jogo jogo = new jogo();
		jogo.newGame();
		jogo.dealer();
		if(jogo.dealer.num > 16) {
			assertEquals(1,1);
		}else {
			fail("Hello");
		}
	}
	
	@Test
	public void testMakeSplit() {
		jogo jogo = new jogo();
		jogo.player.cards[0] = 2;
		jogo.player.cards[1] = 2;
		if(jogo.player.cards[0] == jogo.player.cards[1] && jogo.player.figure[0] == jogo.player.figure[1]  && jogo.player.cards[2] == 0 && jogo.player.cards[0] != 0) {
			assertEquals(1,1);
		}else {
			fail("Hello");
		}
	}
	
	@Test
	public void testSplit() {
		jogo jogo = new jogo();
		jogo.player.cards[0] = 2;
		jogo.player.cards[1] = 2;
		jogo.split();
		if(jogo.player.cards[0] == jogo.split.cards[0] && jogo.player.cards[1] == 0 && jogo.split.cards[1] == 0) {
			assertEquals(1,1);
		}else {
			fail("Hello");
		}
	}
	
	@Test
	public void testGetData() {
		data data = new data();
		data.wins = 100;
		data.losses = 32;
		data.ties = 1;
		data.getData();
		assertEquals(data.played,133);
	}
	
	@Test
	public void testGetDataPercentage() {
		data data = new data();
		data.wins = 100;
		data.losses = 32;
		data.ties = 1;
		data.getData();
		assertEquals(data.percentage,75);
	}
	
	@Test
	public void testSetGetData() {
		ContactProgram contact = new ContactProgram();
		data data = new data();
		data.username = "junit";
		data.password = "junit";
		data.wins = 100;
		data.losses = 32;
		data.ties = 1;
		data.getData();
		data.updateData(0);
		contact.get(data);
		assertEquals(data.wins,100);
	}
	
	@Test
	public void testLogin() {
		data data = new data();
		data.username = "junit";
		data.password = "junit";
		assertEquals(data.login(),true);
	}
	
	

}