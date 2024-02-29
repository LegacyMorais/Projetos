package gamelogic;

import java.awt.Component;

import javax.swing.JOptionPane;

public class data {
	public String username;
	public String password;
	public int wins;
	public int ties;
	public int losses;
	public int played;
	public int percentage;
	public int streak;
	public ContactProgram contact;
	
	public data() {
		this.username = "";
		this.password = "";
		this.wins = 0;
		this.losses = 0;
		this.played = 0;
		this.percentage = 0;
		this.streak = 0;
		this.contact = new ContactProgram();
	}

	public void getData() {
		 played = wins + ties + losses;
		 if(played == 0) {
			 percentage = 0;
		 }else {
			 percentage = (wins*100)/played; 
		 }
		 streak = 0;
	}
	
	
	public void updateData(int gameover) {
		if(gameover==1) {
			wins++;
			streak++;
		}else if(gameover==2) {
			losses++;
			streak = 0;
		}else if(gameover==3) {
			ties++;
			streak = 0;
		}

		 played = wins + ties + losses;
		 if(played == 0) {
			 percentage = 0;
		 }else {
			 percentage = (wins*100)/played; 
		 }
		 
		 contact.set(wins, ties, losses, username);
	}
	
	public boolean login() {
		Component jFrame = null;
		
		if(contact.loginusername(username)) {
			if(contact.loginpassword(username, password)) {
				System.out.println("Login sucessful!");
				return true;
			}else {
				JOptionPane.showMessageDialog(jFrame, "Wrong password!");
				System.out.println("Wrong password");
				return false;
			}
		}else {
			JOptionPane.showMessageDialog(jFrame, "Username doesn't exist");
			System.out.println("Username doesn't exist");
			return false;
		}	
		
	}
	
	public void createAccount() {
		Component jFrame = null;
		if(!contact.loginusername(username)) {
			contact.create(username, password);
			JOptionPane.showMessageDialog(jFrame, "Account created sucessfully!");
			System.out.println("Account created.");
		}else {
			JOptionPane.showMessageDialog(jFrame, "Username already exists!");
			System.out.println("Username already exists");
		}
	}
	
}
