package com.company;

//импорт нужных паккетов
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import static javax.swing.UIManager.setLookAndFeel;


public class TuringMachine extends JFrame  //Наслудеутся от JFrame
{



    public static void main(String [] args) //запуск программы
    {
        new TuringMachine();
    }

    //создаём кнопки и компоненты JLabel для отоброжение цифр
    private JLabel lblTapeLeft;
    private JLabel lblTapeCurrent;
    private JLabel lblTapeRight;
    private JLabel lblState;
    private JButton btnGo;
    private JButton btnStep;
    private JButton btnReset;
    private JButton btnLoad;
    private JButton btnLoadWith;
    private JTextArea proces;
    private String select;

    private String TapeLeft = "";
    private String TapeCurrent = "";
    private String TapeRight = "";
    private String State = "A";
    private ArrayList<String> Program;
    private File ProgramFile;

    public TuringMachine()
    {
        //устанавливаем иконку, размер, названия для нашего приложение
        this.setIconImage(new ImageIcon("img//icon.png").getImage());
        this.setSize(570,200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(" Машина Тьюринга");
        this.setLayout(new BorderLayout());

        try {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //принимает оформление системы для внутренных компонентов
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFrame.setDefaultLookAndFeelDecorated(true); //принимает оформление системы для внешних компонентов

        JPanel panel1 = new JPanel();   //создаём панель для отоброжение цифр
        JPanel panel3 = new JPanel();

        //создаём компоненты для отоброжение цифр и добовляем в панель1
        lblTapeLeft = new JLabel();
        lblTapeCurrent = new JLabel();
        lblTapeRight = new JLabel();
        //Font font = new Font("Courier", Font.PLAIN, 12);
        proces = new JTextArea(2,45);
        lblTapeLeft.setFont(new Font("Monospaced", Font.PLAIN,24));
        lblTapeCurrent.setFont(new Font("Monospaced", Font.BOLD,24));
        lblTapeRight.setFont(new Font("Monospaced", Font.PLAIN,24));
        lblTapeCurrent.setForeground(Color.RED);
        proces.setFont(new Font("Monospaced", Font.PLAIN,12));
        proces.setBackground(null);
        proces.setEditable(false);
        proces.setFocusable(false);
        panel1.add(lblTapeLeft);
        panel1.add(lblTapeCurrent);
        panel1.add(lblTapeRight);
        panel3.add(proces);

        if (ProgramFile==null) // Текст с советом
            proces.setText("                Загрузите Файл");

        JPanel panel2 = new JPanel();    //создаём панель для кнопок

        //далее описоваем кнопки и добовляем в панель2

        lblState = new JLabel("");
        panel2.add(lblState);

        btnGo = new JButton("Запустить");
        btnGo.addActionListener(e -> btnGoClick() );
        btnGo.setEnabled(false);
        panel2.add(btnGo);

        btnStep = new JButton("Шаг");
        btnStep.addActionListener(e -> btnStepClick() );
        btnStep.setEnabled(false);
        panel2.add(btnStep);

        btnReset = new JButton("Сброс");
        btnReset.addActionListener(e -> btnResetClick() );
        panel2.add(btnReset);

        btnLoad = new JButton("Загрузить");
        btnLoad.addActionListener(e -> btnLoadClick() );
        panel2.add(btnLoad);

        btnLoadWith = new JButton("Загрузить Ч С К");
        btnLoadWith.addActionListener(e -> btnLoadClickWithEnd() );
        panel2.add(btnLoadWith);

        this.add(panel1, BorderLayout.NORTH); //добовляем панели в наше окно приложение
        this.add(panel3,BorderLayout.CENTER);
        this.add(panel2, BorderLayout.SOUTH); //

        this.setVisible(true); //видемость окна

        UpdateDisplay();

    }

    private void btnGoClick() //реализация кнопки Выполнить всё
    {
        do //цикл что бы выполнять до остоновки
        {
            ExecuteSingleStep();
        }while (!State.equals("HALT"));
    }

    private void btnStepClick() //реализация кнопки По Шагова
    {
        ExecuteSingleStep();
    }

    private void btnResetClick() //реализация кнопки Сброса
    {
        LoadFile(ProgramFile);
        LoadFileWithEnd(ProgramFile);
        proces.setText("");
        UpdateDisplay();
    }

    private void btnLoadClick()  //реализация кнопки Загрузить
    {
        btnGo.setEnabled(true);
        btnStep.setEnabled(true);
        JFileChooser fc = new JFileChooser();
        int result = fc.showOpenDialog(this);
        ProgramFile = null;
        if (result == JFileChooser.APPROVE_OPTION)
        {
            ProgramFile = fc.getSelectedFile();
            LoadFile(ProgramFile);
        }
    }
    private void btnLoadClickWithEnd()  //реализация кнопки Загрузить
    {
        btnGo.setEnabled(true);
        btnStep.setEnabled(true);
        JFileChooser fc = new JFileChooser();
        int result = fc.showOpenDialog(this);
        ProgramFile = null;
        if (result == JFileChooser.APPROVE_OPTION)
        {
            ProgramFile = fc.getSelectedFile();
            LoadFileWithEnd(ProgramFile);
        }
    }

    private void ExecuteSingleStep() // выполнение шага
    {
        if (!State.equals("HALT"))
        {
            try
            {

             for (String rule : Program)
             {
                if (CheckRule(rule))
                {
                    ExecuteRule(rule);
                    UpdateDisplay();
                    break;
                }
             }
            }catch (Exception ex){  //для вывода ошибки

                showError();
                proces.setText("                Загрузите Файл");


            }
        }
    }

    public void showError(){
        JOptionPane.showMessageDialog(this,
                "   Файл отсутсвует или " +
                        "\n  Неправильно составлен", "Машина Тьюринга",
                JOptionPane.INFORMATION_MESSAGE);
        btnGo.setEnabled(false);
        btnStep.setEnabled(false);
        State = "HALT";
    }

    private boolean CheckRule(String rule)
    {
        String RuleValue = rule.substring(0,1);
        String RuleState = rule.substring(2,3);
        return (RuleValue.equals(TapeCurrent)) & (RuleState.equals(State));
    }


    private void ExecuteRule(String rule)
    {
        String RuleValue = rule.substring(0,1);
        String RuleState = rule.substring(2,3);
        String NewValue = rule.substring(4,5);
        String Move = rule.substring(6, 7);
        String NewState = rule.substring(8, 9);

        // Установка нового значение
        if (!NewValue.equals("*"))
            TapeCurrent = NewValue;

        // Переместить ленту
        if (Move.equals("L")){
            MoveTape("Left");
            Move = "ЛЕВО";
        }
        else if (Move.equals("R")) {
            MoveTape("в Right");
            Move = "в ПРАВО";
        }

        // Остоновить или Установить новое состояние
        if (Move.equals("H")) {
            State = "HALT";
            Move = "НЕ выполняется";
            RuleState = "Остоновлено";
        }
        else
            State = NewState;

        //вывод в консоли
        if(State.equals("HALT")){
            proces.setText("Программа Остоновлено");
        }else {
            proces.setText("История Выполнение:\n" +
                    "Старое значение: " + RuleValue + "  Старое Состояние: " + RuleState + "\n"
                    + "Нынешняя значение: " + select + "  Шаг был: " + Move + "  Нынешняя состояние: " + NewState);
        }
        System.out.println("Выбранное значение: " + RuleValue + "  Состояние: " + RuleState+"\n"
                +"Новое значение: " + NewValue + "  Шаг: " + Move + "  Новое состояние: " + NewState);


    }

    private void MoveTape(String direction)
    {
        if (direction.equals("Left"))
        {
            if (TapeLeft.length() == 0)
                TapeLeft = "_";
            TapeRight = TapeCurrent + TapeRight;
            TapeCurrent = TapeLeft.substring(TapeLeft.length() - 1);
            TapeLeft = TapeLeft.substring(0,TapeLeft.length() - 1);
        }
        else
        {
            if (TapeRight.length() == 0)
                TapeRight = "_";
            TapeLeft = TapeLeft + TapeCurrent;
            TapeCurrent = TapeRight.substring(0,1);
            TapeRight = TapeRight.substring(1);
        }
        UpdateDisplay();

    }

    private void UpdateDisplay()
    {
        if (TapeCurrent.length() == 0)
            TapeCurrent = "_";
        lblTapeLeft.setText(TapeLeft);
        lblTapeCurrent.setText(TapeCurrent);
        lblTapeRight.setText(TapeRight);
        lblState.setText("Сосотояние: " + State + "   ");
        select = TapeCurrent;
    }



    private void LoadFileWithEnd(File file)
    {
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(file));

            // Первая строка - начальное значение ленты
            String TapeLine = in.readLine();
            TapeLeft = "";
            TapeCurrent = TapeLine.substring(0,1); //TapeLine.length() - 2, TapeLine.length()
            TapeRight = TapeLine.substring(1);

            // Первый символ следующей строки - начальное состояние
            String StateLine = in.readLine();
            State = StateLine.substring(0,1);

            // Остальные строки программы
            Program = new ArrayList<String>();
            String ProgramLine;
            do
            {
                ProgramLine = in.readLine();
                if (ProgramLine != null)
                    Program.add(ProgramLine);
            }while (ProgramLine != null);

            in.close();

        }
        catch (Exception ex)
        {
            showError();
        }
        UpdateDisplay();
        proces.setText("");
    }

    /*public void WithEnd(){
        TapeLeft = "";
        TapeCurrent = TapeLine.substring(0,1); //TapeLine.length() - 2, TapeLine.length()
        TapeRight = TapeLine.substring(1);
    }*/

    private void LoadFile(File file)
    {
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(file));

            // Первая строка - начальное значение ленты
            String TapeLine = in.readLine();
            TapeLeft = "";
            TapeCurrent = TapeLine.substring(0,1); //TapeLine.length() - 2, TapeLine.length()
            TapeRight = TapeLine.substring(1);

            // Первый символ следующей строки - начальное состояние
            String StateLine = in.readLine();
            State = StateLine.substring(0,1);

            // Остальные строки программы
            Program = new ArrayList<String>();
            String ProgramLine;
            do
            {
                ProgramLine = in.readLine();
                if (ProgramLine != null)
                    Program.add(ProgramLine);
            }while (ProgramLine != null);

            in.close();

        }
        catch (Exception ex)
        {
            showError();
        }
        UpdateDisplay();
        proces.setText("");
    }


}
