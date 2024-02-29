/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package bet4money;

import javax.swing.ImageIcon;

/**
 *
 * @author pedro
 */
@SuppressWarnings("serial")
public class rules extends javax.swing.JFrame {

    /**
     * Creates new form rules
     */
    public rules() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
   
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        rules = new javax.swing.JTextArea();
        
        java.net.URL iconURL = getClass().getResource("/img/smalllogo.jpg");
        // iconURL is null when not found
        ImageIcon icon = new ImageIcon(iconURL);
        setIconImage(icon.getImage());

        setTitle("Rules");

        rules.setEditable(false);
        rules.setBackground(new java.awt.Color(90, 121, 148));
        rules.setColumns(10);
        rules.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 13)); // NOI18N
        rules.setForeground(new java.awt.Color(255, 255, 255));
        rules.setLineWrap(true);
        rules.setRows(20);
        rules.setText("Objective \n\nPlaying blackjack is easy once you know the rules! All you have to do is beat the dealer by having a higher card total without going over 21.  \n\nCard Values \n\n2 to 10 - Face value\nJ, Q, K - 10 \nAce - 1 or 11  \n\nDefinitions \n\nBlackjack - An Ace and a card worth 10 points (21 total) \nHole - The dealer's card that is face down \nDouble - Double your bet for one additional card (also known as 'Doubling Down')\nHit - Draw another card \nStand - Take no more cards \nBust - Going over 21 ~ \nSplit - Match your initial bet to split your hand into two separate hands Push - Dealer's hand and player's hand are equal  \n\nHow to Play \n\nTo start playing, just adjust your bet amount and then tap on the 'Deal' button to start. The dealer then deals two cards face up to the player. The dealer receives one card face up and one face down. The player then chooses to Hit, Stand or Double. Tap the 'Double' button if you want to double your bet in exchange for only one additional card. You can only do this/her upon receiving your first two cards and doing so will end your turn. Tap the 'Hit' button if you want additional cards. Continue until you desire no more cards. If you don't want any additional cards (or are finished 'hitting'), tap the 'Stand' button. \nOnce you have finished your turn, the dealer reveals his/her hole card and hits or stands as appropriate. The dealer must hit until his/her card total is at least 17. Base your decisions on the assumption that the dealer has a card worth 10 points in the hole. Payouts are issued based on the outcome.  \n\nSplitting \n\nIf you receive two cards of the same number, you can split them into two separate hands by matching your initial bet. Do this/her by tapping the Split button. The dealer will separate your cards and give you an additional card to make each one a complete hand by itself. You will then play each hand separately as you normally would.  \n\nWinning \n\nIf your total is higher than the dealer's without going over 21(or if the dealer busts), you win.  \n\nPayouts \n\nIf you get Blackjack, the dealer pays you 3 to 2. If you and the dealer both get Blackjack, it is a push and no chips are given or taken away. If you have a higher total than the dealer (or the dealer busts), the dealer matches the amount of your chips. If you have a lower total than the dealer (or you bust), the dealer takes your chips. ");
        rules.setWrapStyleWord(true);
        rules.setAlignmentX(0.0F);
        rules.setAlignmentY(0.0F);
        rules.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jScrollPane1.setViewportView(rules);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(rules.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(rules.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(rules.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(rules.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new rules().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea rules;
    // End of variables declaration//GEN-END:variables
}