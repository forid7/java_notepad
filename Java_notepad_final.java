package java_notepad_final;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.StringTokenizer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class Java_notepad_final extends JFrame {

    static JTextArea mainarea;
    JMenuBar mbar;
    JMenu mnuFile, mnuEdit, mnuFormat, mnuHelp;
    JMenuItem itmNew, itmOpen, itmSave, itmSaveas, itmExit;
    JMenuItem itmCut, itmCopy, itmPaste, itmFontColor,
            itmFind, itmReplace, itmFont;

    JCheckBoxMenuItem wordWrap;

    String filename;
    JFileChooser jc;
    String fileContent;
    UndoManager undo;
    UndoAction undoAction;
    RedoAction redoAction;

    String findText;
    int fnext = 1;

    //   public static Java_notepad_final frmMain = new Java_notepad_final();
    FontHelper font;

    public Java_notepad_final() {
        initComponent();
        itmSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });

        itmSaveas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAs();
            }
        });

        itmOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                open();
            }
        });

        itmNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                open_new();
            }
        });

        itmExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        itmCut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainarea.cut();
            }
        });
        itmCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainarea.copy();
            }
        });
        itmPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainarea.paste();
            }
        });

        mainarea.getDocument().addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undo.addEdit(e.getEdit());
                undoAction.update();
                redoAction.update();
            }
        });

        itmFind.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FindAndReplace(null, false);
            }
        });
        itmReplace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FindAndReplace(null, true);
            }
        });

        //  mainarea.setWrapStyleWord(true);
        wordWrap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (wordWrap.isSelected()) {
                    mainarea.setLineWrap(true);
                    mainarea.setWrapStyleWord(true);
                } else {
                    mainarea.setLineWrap(false);
                    mainarea.setWrapStyleWord(false);
                }
            }
        });

        itmFontColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color c = JColorChooser.showDialog(rootPane, "choose a color", Color.BLACK);
                mainarea.setForeground(c);
            }
        });

        itmFont.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                font.setVisible(true);
            }
        });
        font.getOK().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainarea.setFont(font.font());
                font.setVisible(false);
            }
        });
        font.getCancel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                font.setVisible(false);
            }
        });

    }

    private void initComponent() {
        jc = new JFileChooser(".");

        mainarea = new JTextArea();

        undo = new UndoManager();
        font = new FontHelper();
        ImageIcon iconUndo = new ImageIcon(getClass().getResource("/img/undo.png"));
        ImageIcon iconRedo = new ImageIcon(getClass().getResource("/img/redo.png"));

        undoAction = new UndoAction(iconUndo);
        redoAction = new RedoAction(iconRedo);

        getContentPane().add(mainarea);
        getContentPane().add(new JScrollPane(mainarea), BorderLayout.CENTER);
        setTitle("Untitled Notepad");
        setSize(800, 600);
        

        //menu bar
        mbar = new JMenuBar();

        //menu
        mnuFile = new JMenu("File");
        mnuEdit = new JMenu("Edit");
        mnuFormat = new JMenu("Format");
        mnuHelp = new JMenu("Help");

        //add icon to menu item
        ImageIcon iconNew = new ImageIcon(getClass().getResource("/img/new.png"));
        ImageIcon iconOpen = new ImageIcon(getClass().getResource("/img/open.png"));
        ImageIcon iconSave = new ImageIcon(getClass().getResource("/img/save.png"));
        ImageIcon iconSaveAs = new ImageIcon(getClass().getResource("/img/saveas.png"));
        ImageIcon iconCut = new ImageIcon(getClass().getResource("/img/cut.png"));
        ImageIcon iconCopy = new ImageIcon(getClass().getResource("/img/copy.png"));
        ImageIcon iconPaste = new ImageIcon(getClass().getResource("/img/paste.png"));
        ImageIcon iconFind = new ImageIcon(getClass().getResource("/img/find.png"));
        ImageIcon iconReplace = new ImageIcon(getClass().getResource("/img/replace.png"));
        ImageIcon iconFont = new ImageIcon(getClass().getResource("/img/font.png"));

        //menuItem
        itmNew = new JMenuItem("New", iconNew);
        itmOpen = new JMenuItem("Open", iconOpen);
        itmSave = new JMenuItem("Save", iconSave);
        itmSaveas = new JMenuItem("Saveas", iconSaveAs);
        itmExit = new JMenuItem("Exit");

        itmCopy = new JMenuItem("Copy", iconCopy);
        itmCut = new JMenuItem("Cut", iconCut);
        itmPaste = new JMenuItem("Paste", iconPaste);

        itmFind = new JMenuItem("Find", iconFind);
        itmReplace = new JMenuItem("Replace", iconReplace);

        itmFontColor = new JMenuItem("Font Color");

        itmFont = new JMenuItem("Font", iconFont);

        wordWrap = new JCheckBoxMenuItem("Word Wrap");

        //adding shortcut
        itmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        itmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        itmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        itmFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        itmReplace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        itmCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        itmCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        itmPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        
        
       
        //add menuitem
        mnuFile.add(itmNew);
        mnuFile.add(itmOpen);
        mnuFile.add(itmSave);
        mnuFile.add(itmSaveas);
        mnuFile.addSeparator();
        mnuFile.add(itmExit);
        mnuEdit.add(undoAction);
        mnuEdit.add(redoAction);
        mnuEdit.add(itmCut);
        mnuEdit.add(itmCopy);
        mnuEdit.add(itmPaste);
        mnuEdit.add(itmFind);
        mnuEdit.add(itmReplace);
        mnuEdit.add(itmFont);
        mnuFormat.add(wordWrap);
        mnuFormat.add(itmFontColor);

        //add menu file
        mbar.add(mnuFile);
        mbar.add(mnuEdit);
        mbar.add(mnuFormat);
        mbar.add(mnuHelp);

        //add menu bar
        setJMenuBar(mbar);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }

    private void save() {
        PrintWriter fout = null;
        //  int retval = -1;
        try {
            if (filename == null) {
                saveAs();
            } else {
                fout = new PrintWriter(new FileWriter(filename));
                String s = mainarea.getText();
                StringTokenizer st = new StringTokenizer(s, System.getProperty("line.separator"));
                while (st.hasMoreElements()) {
                    fout.println(st.nextToken());
                }
                fileContent = mainarea.getText();
                JOptionPane.showMessageDialog(rootPane, "File Saved");

            }

        } catch (IOException e) {
        } finally {
            if (fout != null) {
                fout.close();
            }

        }

    }

    private void saveAs() {
        PrintWriter fout = null;
        int retval = -1;
        try {
            retval = jc.showSaveDialog(this);
            if (retval == JFileChooser.APPROVE_OPTION) {

                //   File file = jc.getSelectedFile();
                if (jc.getSelectedFile().exists()) {
                    int option = JOptionPane.showConfirmDialog(rootPane, "Do you replace this file?",
                            "Confirmation", JOptionPane.OK_CANCEL_OPTION);

                    if (option == 0) {
                        fout = new PrintWriter(new FileWriter(jc.getSelectedFile()));
                        String s = mainarea.getText();
                        StringTokenizer st = new StringTokenizer(s, System.getProperty("line.separator"));
                        while (st.hasMoreElements()) {
                            fout.println(st.nextToken());
                        }
                        fileContent = mainarea.getText();
                        JOptionPane.showMessageDialog(rootPane, "File Saved");
                        filename = jc.getSelectedFile().getName();
                        setTitle(filename);

                    } else {
                        saveAs();
                    }
                } else {
                    fout = new PrintWriter(new FileWriter(jc.getSelectedFile()));
                    String s = mainarea.getText();
                    StringTokenizer st = new StringTokenizer(s, System.getProperty("line.separator"));
                    while (st.hasMoreElements()) {
                        fout.println(st.nextToken());
                    }
                    fileContent = mainarea.getText();
                    JOptionPane.showMessageDialog(rootPane, "File Saved");
                    filename = jc.getSelectedFile().getName();
                    setTitle(filename);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            fout.close();
        }
    }

    private void open() {
        try {
            int retval = jc.showOpenDialog(this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                mainarea.setText(null);
                Reader in = new FileReader(jc.getSelectedFile());
                char[] buff = new char[10000000];
                int nch;
                while ((nch = in.read(buff, 0, buff.length)) != -1) {
                    mainarea.append(new String(buff, 0, nch));
                }
                filename = jc.getSelectedFile().getName();
                setTitle(filename);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void open_new() {
        if (!mainarea.getText().equals("") && !mainarea.getText().equals(fileContent)) {
            if (filename == null) {
                int option = JOptionPane.showConfirmDialog(rootPane, "Do you want to save the changes?");
                if (option == 0) {
                    saveAs();
                    clear();
                } else if (option == 2) {

                } else {
                    clear();
                }
            } else {
                int option = JOptionPane.showConfirmDialog(rootPane, "Do you want to save the changes?");
                if (option == 0) {
                    save();
                    clear();
                } else if (option == 2) {

                } else {
                    clear();
                }
            }
        } else {
            clear();
        }
    }

    private void clear() {
        mainarea.setText(null);
        setTitle("Untitled Notepad");
        filename = null;
        fileContent = null;
    }

    class UndoAction extends AbstractAction {

        public UndoAction(ImageIcon undoIcon) {
            super("undo", undoIcon);
            setEnabled(false);

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                undo.undo();
            } catch (CannotUndoException ex) {
                ex.printStackTrace();
            }
            update();
            redoAction.update();
        }

        protected void update() {
            if (undo.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, "undo");
            } else {
                setEnabled(false);
                putValue(Action.NAME, "undo");
            }
        }

    }

    class RedoAction extends AbstractAction {

        public RedoAction(ImageIcon redoIcon) {
            super("redo", redoIcon);
            setEnabled(false);

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                undo.redo();
            } catch (CannotRedoException ex) {
                ex.printStackTrace();
            }
            update();
            undoAction.update();
        }

        protected void update() {
            if (undo.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, "redo");
            } else {
                setEnabled(false);
                putValue(Action.NAME, "redo");
            }
        }

    }
    
    

    public static void main(String[] args) {
        Java_notepad_final jn = new Java_notepad_final();

    }

    public static JTextArea getArea() {
        return mainarea;
    }

}
