package java_notepad_final;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class FindAndReplace extends JDialog implements ActionListener {

    boolean foundOne, isReplace;
    JTextField searchText, replaceText;
    JCheckBox cbCase, cbWhole;
    JRadioButton up, down;
    JLabel statusinfo;
    JFrame owner;
    JPanel north, center, south;

    public FindAndReplace(JFrame owner, boolean isReplace) {
        super(owner, true);
        this.isReplace = isReplace;

        north = new JPanel();
        center = new JPanel();
        south = new JPanel();

        if (isReplace) {
            setTitle("Find and Replace");
            setReplacePanel(north);
        } else {
            setTitle("Find");
            setFindPanel(north);
        }

        addComponent(center);
        statusinfo = new JLabel("Status Info:");
        south.add(statusinfo);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }

        });

        getContentPane().add(north, BorderLayout.NORTH);
        getContentPane().add(center, BorderLayout.CENTER);
        getContentPane().add(south, BorderLayout.SOUTH);

        pack();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
//        int x= (owner.getWidth()*3/5)-(getWidth()/2);
//        int y= (owner.getHeight()*3/5)-(getHeight()/2);
//        setLocation(x, y);
//        int x= ((owner.getWidth())*3/5)-(getWidth()/2);
//        int y= ((owner.getHeight())*3/5)-(getHeight()/2);
//        setLocation(x, y);
        setVisible(true);

    }

    public void addComponent(JPanel center) {
        JPanel east = new JPanel();
        JPanel west = new JPanel();
        center.setLayout(new GridLayout(1, 2));
        east.setLayout(new GridLayout(2, 1));
        west.setLayout(new GridLayout(2, 1));
        cbCase = new JCheckBox("Match Case", true);
        cbWhole = new JCheckBox("Match Word", true);
        ButtonGroup group = new ButtonGroup();
        up = new JRadioButton("search UP", false);
        down = new JRadioButton("search Down", true);
        group.add(up);
        group.add(down);

        east.add(cbCase);
        east.add(cbWhole);
        east.setBorder(BorderFactory.createTitledBorder("search option:"));
        west.add(up);
        west.add(down);
        west.setBorder(BorderFactory.createTitledBorder("search direction:"));
        center.add(east);
        center.add(west);
    }

    private void setFindPanel(JPanel north) {
        final JButton NEXT = new JButton("Find Next");
        NEXT.addActionListener(this);
        NEXT.setEnabled(false);
        searchText = new JTextField(20);
        searchText.addActionListener(this);
        searchText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                boolean state = (searchText.getDocument().getLength() > 0);
                NEXT.setEnabled(state);
                foundOne = false;
            }

        });

        if (searchText.getText().length() > 0) {
            NEXT.setEnabled(true);
        }
        north.add(new JLabel("Find word:"));
        north.add(searchText);
        north.add(NEXT);
    }

    private void setReplacePanel(JPanel north) {
        GridBagLayout grid = new GridBagLayout();
        north.setLayout(grid);
        GridBagConstraints con = new GridBagConstraints();
        con.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblFword = new JLabel("Find Word");
        JLabel lblRword = new JLabel("Find Word");
        final JButton NEXT = new JButton("Replace Text");
        NEXT.addActionListener(this);
        NEXT.setEnabled(false);
        final JButton REPLACE = new JButton("Replace All");
        REPLACE.addActionListener(this);
        REPLACE.setEnabled(false);
        searchText = new JTextField(20);
        replaceText = new JTextField(20);

        replaceText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                boolean state = (replaceText.getDocument().getLength() > 0);

                NEXT.setEnabled(state);
                REPLACE.setEnabled(state);
                foundOne = false;
            }

        });

        con.gridx = 0;
        con.gridy = 0;
        grid.setConstraints(lblFword, con);
        north.add(lblFword);
        con.gridx = 1;
        con.gridy = 0;
        grid.setConstraints(searchText, con);
        north.add(searchText);
        con.gridx = 2;
        con.gridy = 0;
        grid.setConstraints(NEXT, con);
        north.add(NEXT);
        con.gridx = 0;
        con.gridy = 1;
        grid.setConstraints(lblRword, con);
        north.add(lblRword);
        con.gridx = 1;
        con.gridy = 1;
        grid.setConstraints(replaceText, con);
        north.add(replaceText);
        con.gridx = 2;
        con.gridy = 1;
        grid.setConstraints(REPLACE, con);
        north.add(REPLACE);

    }

    private void setFindAndReplacePanel() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(searchText) || e.getSource().equals(replaceText)) {
            validate();
        }
        process();
        if(e.getActionCommand().equals("Replace All")){
           replaceAll();
        }
    }

    private void process() {
        if (isReplace) {
            statusinfo.setText("Replacing" + searchText.getText());
        } else {
            statusinfo.setText("Searching for" + searchText.getText());
        }

        int caret = Java_notepad_final.getArea().getCaretPosition();
        String word = getWord();
        String text = getAllText();
        caret = search(text, word, caret);
        if (caret < 0) {
            endResult(false, 0);
        }

    }

    private void endResult(boolean isReplaceAll, int tally) {
        String messege = "";
        if (isReplaceAll) {
              if(tally==0){
                  messege= searchText.getText()+" not found ";
              }
              else if(tally==1)
              {
              messege= "One change was made to"+ searchText.getText();
              }
              else
              {
              messege= ""+tally+ " changes were made to "+ searchText.getText(); 
              }
        } else {
            String str = "";
            if (isSearchDown()) {
                str = "search down";
            } else {
                str = "search up";
            }
            if (foundOne && !isReplace) {
                messege = "End of" + str + "for" + searchText.getText();
            } else if (foundOne && isReplace) {
                messege = "End of Replace" + searchText.getText() + " with " + replaceText.getText();
            }
        }
        statusinfo.setText(messege);
    }

    private int search(String text, String word, int caret) {
        boolean found = false;
        int all = text.length();
        int check = word.length();
        if (isSearchDown()) {
            int add = 0;

            for (int i = caret + 1; i < (all-check ); i++) {

                String temp = text.substring(i, (i + check));
                if (temp.equals(word)) {
                    if (wholeWordIsSelected()) {
                        if (checkForWholeWord(check, text, add, caret)) {
                            caret = i;
                            found = true;
                            break;
                        } else {

                        }
                    } else {//not whole word
                        caret = i;
                        found = true;
                        break;
                    }
                }

            }
        } else {
            int add = caret;
            for (int i = caret - 1; i >= check; i--) {
                add--;
                String temp = text.substring((i - check), i);

                if (temp.equals(word)) {
                    if (wholeWordIsSelected()) {
                        if (checkForWholeWord(check, text, add, caret)) {
                            caret = i;
                            found = true;
                            break;
                        } else {

                        }
                    } else {//not whole word
                        caret = i;
                        found = true;
                        break;
                    }
                }

            }
        }
        Java_notepad_final.getArea().setCaretPosition(0);

        if (found) {
            Java_notepad_final.getArea().requestFocus();
            if (isSearchDown()) {
                Java_notepad_final.getArea().select(caret, caret + check);
            } else {
                Java_notepad_final.getArea().select(caret - check, caret);
            }
            
             //for replace
        if (isReplace) {
            String replace = replaceText.getText();
            Java_notepad_final.getArea().replaceSelection(replace);
            if (isSearchDown()) {
                System.out.println(caret);
                System.out.println(check);
                Java_notepad_final.getArea().select(caret, check + replace.length());
            } else {
                Java_notepad_final.getArea().select(caret - replace.length(), caret);
            }
        }
            
            foundOne = true;
            return caret;
        }
       

        return -1;
    }

    private String getAllText() {
        if (caseNotSelected()) {
            return Java_notepad_final.getArea().getText().toLowerCase();
        }

        return Java_notepad_final.getArea().getText();
    }

    private String getWord() {
        if (caseNotSelected()) {
            return searchText.getText().toLowerCase();
        }
        return searchText.getText();
    }

    private boolean caseNotSelected() {
        return !cbCase.isSelected();
    }

    private boolean isSearchDown() {
        return down.isSelected();
    }

    private boolean wholeWordIsSelected() {
        return cbWhole.isSelected();
    }

    private boolean checkForWholeWord(int check, String text, int add, int caret) {
        int offsetLeft = (caret + add) - 1;
        int offsetRight = (caret + add) + check;
        if ((offsetLeft < 0) || offsetRight > text.length()) {
            return true;
        }
        return ((!Character.isLetterOrDigit(text.charAt(offsetLeft)))
                && (!Character.isLetterOrDigit(text.charAt(offsetRight))));

    }
    
    private void replaceAll()
    {
    String word= searchText.getText();
    String text= Java_notepad_final.getArea().getText();
    String insert = replaceText.getText();
    StringBuffer sb= new StringBuffer(text);
    int diff = insert.length()-word.length();
    int offset= 0;
    int tally=0;
        for (int i = 0; i < text.length()-word.length(); i++) {
            String temp = text.substring(i,i+word.length());
            if((temp.equals(word)&& checkForWholeWord(word.length(),text,0,i))){
            tally++;
             sb.replace(i+offset, i+ offset+word.length(), insert);
             offset+=diff;
            }
            
        }
        Java_notepad_final.getArea().setText(sb.toString());
        endResult(true, tally);
        Java_notepad_final.getArea().setCaretPosition(0);
    }

}
