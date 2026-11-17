package programmingProject;
import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Java100Examples —— 100 道 Java 例题 · 单文件贯通全书
 * 每个例子是 public static 嵌套类 DemoNN,内含 main。
 * 运行:  java Java100Examples <1-100>
 */
public class demo {

    // ============ 第1章 Java基础语法(1-15) ============

    public static class Demo01 {
        public static void main(String[] args) {
            System.out.println("Hello, Java!");
        }
    }

    public static class Demo02 {
        public static void main(String[] args) {
            byte b = 100; short s = 10000; int i = 100000; long l = 123456789L;
            float f = 3.14f; double d = 3.14159; char c = 'A'; boolean flag = true;
            System.out.println(b + " " + s + " " + i + " " + l);
            System.out.println(f + " " + d + " " + c + " " + flag);
        }
    }

    public static class Demo03 {
        public static void main(String[] args) {
            int a = 100; long b = a; double c = b;
            int d = (int) 3.99;
            byte e = (byte) 200;
            System.out.println(b + "," + c + "," + d + "," + e);
        }
    }

    public static class Demo04 {
        public static void main(String[] args) {
            int a = 17, b = 5;
            System.out.println("和=" + (a + b) + " 差=" + (a - b) + " 积=" + (a * b));
            System.out.println("商=" + (a / b) + " 余=" + (a % b));
            System.out.println("奇偶:" + (a % 2 == 0 ? "偶" : "奇"));
        }
    }

    public static class Demo05 {
        public static void main(String[] args) {
            int x = 23, y = 45, z = 12;
            int max = x > y ? (x > z ? x : z) : (y > z ? y : z);
            System.out.println("最大值:" + max);
        }
    }

    public static class Demo06 {
        public static void main(String[] args) {
            int a = 10, b = 20;
            int t = a; a = b; b = t;
            a = a + b; b = a - b; a = a - b;
            a = a ^ b; b = a ^ b; a = a ^ b;
            System.out.println("a=" + a + ", b=" + b);
        }
    }

    public static class Demo07 {
        public static void main(String[] args) {
            Scanner sc = new Scanner(System.in);
            System.out.print("请输入姓名:");
            String name = sc.nextLine();
            System.out.print("请输入年龄:");
            int age = sc.nextInt();
            System.out.printf("%s 今年 %d 岁%n", name, age);
            sc.close();
        }
    }

    public static class Demo08 {
        static final double PI = 3.1415926;
        static final int MAX_SIZE = 100;
        public static void main(String[] args) {
            double r = 5;
            System.out.printf("面积=%.2f, 周长=%.2f%n", PI * r * r, 2 * PI * r);
            System.out.println("MAX_SIZE=" + MAX_SIZE);
        }
    }

    public static class Demo09 {
        public static void main(String[] args) {
            int i = 5;
            int a = i++;
            int b = ++i;
            System.out.println("a=" + a + ", b=" + b + ", i=" + i);
        }
    }

    public static class Demo10 {
        public static void main(String[] args) {
            int a = 6, b = 3;
            System.out.println("与:" + (a & b));
            System.out.println("或:" + (a | b));
            System.out.println("异或:" + (a ^ b));
            System.out.println("取反:" + (~a));
            System.out.println("左移:" + (a << 1));
            System.out.println("右移:" + (a >> 1));
        }
    }

    public static class Demo11 {
        public static void main(String[] args) {
            String name = "张三"; int age = 20; double score = 92.5;
            System.out.println("姓名:" + name + ", 年龄:" + age);
            System.out.printf("姓名:%s, 年龄:%d, 成绩:%.1f%n", name, age, score);
            System.out.println(String.format("成绩等级:%c", 'A'));
        }
    }

    public static class Demo12 {
        public static void main(String[] args) {
            int max = Integer.MAX_VALUE;
            System.out.println("溢出:" + (max + 1));
            System.out.println("安全:" + (max + 1L));
            long big = 10_0000_0000L;
            System.out.println("下划线分隔:" + big);
        }
    }

    public static class Demo13 {
        public static void main(String[] args) {
            Random r = new Random();
            System.out.println("随机整数:" + r.nextInt(100));
            String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
            StringBuilder code = new StringBuilder();
            for (int i = 0; i < 6; i++) code.append(chars.charAt(r.nextInt(chars.length())));
            System.out.println("验证码:" + code);
        }
    }

    public static class Demo14 {
        public static void main(String[] args) {
            Scanner sc = new Scanner(System.in);
            System.out.print("体重(kg):"); double w = sc.nextDouble();
            System.out.print("身高(m):"); double h = sc.nextDouble();
            double bmi = w / (h * h);
            String level = bmi < 18.5 ? "偏瘦" : bmi < 24 ? "正常" : bmi < 28 ? "偏胖" : "肥胖";
            System.out.printf("BMI=%.2f, 等级:%s%n", bmi, level);
        }
    }

    public static class Demo15 {
        static double c2f(double c) { return c * 9 / 5.0 + 32; }
        static double f2c(double f) { return (f - 32) * 5 / 9.0; }
        public static void main(String[] args) {
            System.out.printf("37℃ = %.1f℉%n", c2f(37));
            System.out.printf("98.6℉ = %.1f℃%n", f2c(98.6));
        }
    }

    // ============ 第2章 流程控制(16-25) ============

    public static class Demo16 {
        public static void main(String[] args) {
            int year = 2024;
            boolean leap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
            System.out.println(year + (leap ? " 是闰年" : " 不是闰年"));
        }
    }

    public static class Demo17 {
        public static void main(String[] args) {
            int score = 86;
            if (score < 0 || score > 100) System.out.println("非法成绩");
            else if (score >= 90) System.out.println("优秀");
            else if (score >= 80) System.out.println("良好");
            else if (score >= 60) System.out.println("及格");
            else System.out.println("不及格");
        }
    }

    public static class Demo18 {
        public static void main(String[] args) {
            int day = 3;
            String name = switch (day) {
                case 1 -> "周一";
                case 2 -> "周二";
                case 3 -> "周三";
                case 4, 5 -> "工作日";
                case 6, 7 -> "周末";
                default -> "非法";
            };
            System.out.println(name);
        }
    }

    public static class Demo19 {
        public static void main(String[] args) {
            int sum = 0; long fact = 1;
            for (int i = 1; i <= 10; i++) { sum += i; fact *= i; }
            System.out.println("1~10和=" + sum);
            System.out.println("10!=" + fact);
        }
    }

    public static class Demo20 {
        public static void main(String[] args) {
            int target = new Random().nextInt(100) + 1;
            Scanner sc = new Scanner(System.in);
            int guess, count = 0;
            do {
                System.out.print("猜一个1-100的数:");
                guess = sc.nextInt(); count++;
                if (guess > target) System.out.println("大了");
                else if (guess < target) System.out.println("小了");
            } while (guess != target);
            System.out.println("猜对了!共 " + count + " 次");
        }
    }

    public static class Demo21 {
        public static void main(String[] args) {
            int i = 100;
            do { System.out.println("do-while 会执行一次, i=" + i); i++; } while (i < 10);
            while (i < 10) System.out.println("while 不会执行");
        }
    }

    public static class Demo22 {
        public static void main(String[] args) {
            for (int i = 1; i <= 9; i++) {
                for (int j = 1; j <= i; j++) System.out.printf("%d×%d=%-4d", j, i, i * j);
                System.out.println();
            }
        }
    }

    public static class Demo23 {
        public static void main(String[] args) {
            int n = 5;
            for (int i = 1; i <= n; i++)
                System.out.println(" ".repeat(n - i) + "*".repeat(2 * i - 1));
            for (int i = n - 1; i >= 1; i--)
                System.out.println(" ".repeat(n - i) + "*".repeat(2 * i - 1));
        }
    }

    public static class Demo24 {
        public static void main(String[] args) {
            int count = 0;
            for (int n = 2; n <= 100; n++) {
                boolean prime = true;
                for (int i = 2; i * i <= n; i++) if (n % i == 0) { prime = false; break; }
                if (!prime) continue;
                System.out.print(n + " "); count++;
            }
            System.out.println("\n2~100共有 " + count + " 个素数");
        }
    }

    public static class Demo25 {
        public static void main(String[] args) {
            for (int x = 0; x <= 20; x++)
                for (int y = 0; y <= 33; y++) {
                    int z = 100 - x - y;
                    if (z % 3 == 0 && 5 * x + 3 * y + z / 3 == 100)
                        System.out.printf("公鸡=%d 母鸡=%d 小鸡=%d%n", x, y, z);
                }
        }
    }

    // ============ 第3章 数组(26-33) ============

    public static class Demo26 {
        public static void main(String[] args) {
            int[] arr = {45, 78, 23, 90, 67, 12};
            int max = arr[0], min = arr[0], sum = 0;
            for (int v : arr) { max = Math.max(max, v); min = Math.min(min, v); sum += v; }
            System.out.println("max=" + max + " min=" + min + " avg=" + (double) sum / arr.length);
        }
    }

    public static class Demo27 {
        public static void main(String[] args) {
            int[] arr = {1, 2, 3, 4, 5};
            for (int i = 0, j = arr.length - 1; i < j; i++, j--) {
                int t = arr[i]; arr[i] = arr[j]; arr[j] = t;
            }
            System.out.println(Arrays.toString(arr));
        }
    }

    public static class Demo28 {
        public static void main(String[] args) {
            int[] arr = {5, 2, 9, 1, 7, 3};
            for (int i = 0; i < arr.length - 1; i++) {
                boolean swapped = false;
                for (int j = 0; j < arr.length - 1 - i; j++)
                    if (arr[j] > arr[j + 1]) {
                        int t = arr[j]; arr[j] = arr[j + 1]; arr[j + 1] = t; swapped = true;
                    }
                if (!swapped) break;
            }
            System.out.println(Arrays.toString(arr));
        }
    }

    public static class Demo29 {
        public static void main(String[] args) {
            int[] arr = {64, 25, 12, 22, 11};
            for (int i = 0; i < arr.length - 1; i++) {
                int minIdx = i;
                for (int j = i + 1; j < arr.length; j++) if (arr[j] < arr[minIdx]) minIdx = j;
                int t = arr[i]; arr[i] = arr[minIdx]; arr[minIdx] = t;
            }
            System.out.println(Arrays.toString(arr));
        }
    }

    public static class Demo30 {
        static int binarySearch(int[] a, int target) {
            int l = 0, r = a.length - 1;
            while (l <= r) {
                int mid = (l + r) >>> 1;
                if (a[mid] == target) return mid;
                if (a[mid] < target) l = mid + 1; else r = mid - 1;
            }
            return -1;
        }
        public static void main(String[] args) {
            int[] arr = {1, 3, 5, 7, 9, 11, 13};
            System.out.println("索引:" + binarySearch(arr, 9));
        }
    }

    public static class Demo31 {
        public static void main(String[] args) {
            int n = 6;
            int[][] a = new int[n][];
            for (int i = 0; i < n; i++) {
                a[i] = new int[i + 1];
                a[i][0] = a[i][i] = 1;
                for (int j = 1; j < i; j++) a[i][j] = a[i - 1][j - 1] + a[i - 1][j];
            }
            for (int[] row : a) {
                for (int v : row) System.out.printf("%-4d", v);
                System.out.println();
            }
        }
    }

    public static class Demo32 {
        public static void main(String[] args) {
            int[] src = {1, 2, 3, 4, 5};
            int[] a = new int[8];
            System.arraycopy(src, 0, a, 0, src.length);
            int[] b = Arrays.copyOf(src, 10);
            int[] c = src.clone();
            System.out.println(Arrays.toString(a));
            System.out.println(Arrays.toString(b));
            System.out.println(Arrays.toString(c));
        }
    }

    public static class Demo33 {
        public static void main(String[] args) {
            int[] arr = {1, 2, 2, 3, 3, 3, 4, 4, 4, 4};
            Map<Integer, Integer> map = new HashMap<>();
            for (int v : arr) map.merge(v, 1, Integer::sum);
            map.forEach((k, v) -> System.out.println(k + " 出现 " + v + " 次"));
        }
    }

    // ============ 第4章 面向对象基础(34-45) ============

    public static class Demo34 {
        static class Person {
            String name; int age;
            void introduce() { System.out.println("我叫" + name + ",今年" + age + "岁"); }
        }
        public static void main(String[] args) {
            Person p = new Person();
            p.name = "张三"; p.age = 20; p.introduce();
        }
    }

    public static class Demo35 {
        static class Student {
            String name; int age;
            Student() { this("未知", 0); }
            Student(String name) { this(name, 18); }
            Student(String name, int age) { this.name = name; this.age = age; }
            void show() { System.out.println(name + "/" + age); }
        }
        public static void main(String[] args) {
            new Student().show();
            new Student("李四").show();
            new Student("王五", 22).show();
        }
    }

    public static class Demo36 {
        static class Account {
            private String id; private double balance;
            public String getId() { return id; }
            public void setId(String id) {
                if (id == null || id.isBlank()) throw new IllegalArgumentException("id不能为空");
                this.id = id;
            }
            public double getBalance() { return balance; }
            public void deposit(double money) {
                if (money <= 0) throw new IllegalArgumentException("金额必须>0");
                balance += money;
            }
        }
        public static void main(String[] args) {
            Account a = new Account();
            a.setId("A001"); a.deposit(100);
            System.out.println(a.getId() + " 余额:" + a.getBalance());
        }
    }

    public static class Demo37 {
        static class User {
            private String name; private int age;
            public User(String name, int age) { this.name = name; this.age = age; }
            public User copy() { return new User(this.name, this.age); }
            public String toString() { return "User(" + name + "," + age + ")"; }
        }
        public static void main(String[] args) {
            User u = new User("Tom", 18);
            System.out.println(u.copy());
        }
    }

    public static class Demo38 {
        static class Counter {
            private static int count = 0;
            private final int id;
            Counter() { id = ++count; }
            static int getCount() { return count; }
            int getId() { return id; }
        }
        public static void main(String[] args) {
            new Counter(); new Counter(); new Counter();
            System.out.println("创建对象数:" + Counter.getCount());
        }
    }

    public static class Demo39 {
        static class Calc {
            int add(int a, int b) { return a + b; }
            double add(double a, double b) { return a + b; }
            int add(int a, int b, int c) { return a + b + c; }
            String add(String a, String b) { return a + b; }
        }
        public static void main(String[] args) {
            Calc c = new Calc();
            System.out.println(c.add(1, 2));
            System.out.println(c.add(1.5, 2.5));
            System.out.println(c.add(1, 2, 3));
            System.out.println(c.add("He", "llo"));
        }
    }

    public static class Demo40 {
        static class BankAccount {
            private final String no; private double balance; private int transCount;
            BankAccount(String no, double init) { this.no = no; this.balance = init; }
            void deposit(double m) {
                if (m <= 0) throw new IllegalArgumentException("金额非法");
                balance += m; transCount++;
            }
            void withdraw(double m) {
                if (m <= 0 || m > balance) throw new IllegalArgumentException("余额不足或金额非法");
                balance -= m; transCount++;
            }
            void print() { System.out.printf("账户%s 余额:%.2f 交易次数:%d%n", no, balance, transCount); }
        }
        public static void main(String[] args) {
            BankAccount acc = new BankAccount("6222-0001", 1000);
            acc.deposit(500); acc.withdraw(200); acc.print();
        }
    }

    public static class Demo41 {
        static class Book {
            String title; double price;
            Book(String title, double price) { this.title = title; this.price = price; }
        }
        public static void main(String[] args) {
            Book[] books = {
                    new Book("Java基础", 59.9),
                    new Book("数据结构", 79.0),
                    new Book("算法导论", 128.0)
            };
            double total = 0;
            for (Book b : books) { System.out.println(b.title + " - " + b.price); total += b.price; }
            System.out.println("总价:" + total);
        }
    }

    public static class Demo42 {
        static class Product {
            String name; double price;
            Product(String name, double price) { this.name = name; this.price = price; }
            public String toString() { return name + ":" + price; }
        }
        public static void main(String[] args) {
            Product[] arr = { new Product("A", 30), new Product("B", 10), new Product("C", 20) };
            Arrays.sort(arr, (x, y) -> Double.compare(x.price, y.price));
            System.out.println(Arrays.toString(arr));
        }
    }

    public static class Demo43 {
        public static void main(String[] args) {
            List<String> list = Arrays.asList("a", "b", "c");
            java.util.List<String> list2 = new ArrayList<>(list);
            System.out.println(list2);
        }
    }

    public static class Demo44 {
        private int outer = 10;
        class Inner { void show() { System.out.println("访问外部:" + outer); } }
        static class StaticInner { void show() { System.out.println("静态内部类"); } }
        public static void main(String[] args) {
            Demo44 d = new Demo44();
            Demo44.Inner in = d.new Inner();
            in.show();
            new Demo44.StaticInner().show();
        }
    }

    public static class Demo45 {
        interface Greeting { void say(String name); }
        public static void main(String[] args) {
            Greeting g1 = new Greeting() {
                @Override public void say(String name) { System.out.println("你好," + name); }
            };
            g1.say("张三");
            Greeting g2 = name -> System.out.println("Hi, " + name);
            g2.say("李四");
        }
    }

    // ============ 第5章 继承·多态·接口(46-55) ============

    public static class Demo46 {
        static class Animal { void speak() { System.out.println("动物叫"); } }
        static class Dog extends Animal { @Override void speak() { System.out.println("汪汪"); } }
        static class Cat extends Animal { @Override void speak() { System.out.println("喵喵"); } }
        public static void main(String[] args) {
            new Animal().speak(); new Dog().speak(); new Cat().speak();
        }
    }

    public static class Demo47 {
        static class Father {
            String name = "父类";
            Father() { System.out.println("Father 构造"); }
            void hello() { System.out.println("父类 hello"); }
        }
        static class Son extends Father {
            String name = "子类";
            Son() { super(); System.out.println("Son 构造"); }
            void hello() {
                super.hello();
                System.out.println("子类 hello, name=" + name + ", super.name=" + super.name);
            }
        }
        public static void main(String[] args) { new Son().hello(); }
    }

    public static class Demo48 {
        static class Shape {
            double area() { return 0; }
            public String toString() { return "面积=" + area(); }
        }
        static class Circle extends Shape {
            double r; Circle(double r) { this.r = r; }
            @Override double area() { return Math.PI * r * r; }
        }
        static class Rect extends Shape {
            double w, h; Rect(double w, double h) { this.w = w; this.h = h; }
            @Override double area() { return w * h; }
        }
        public static void main(String[] args) {
            Shape[] shapes = { new Circle(2), new Rect(3, 4) };
            for (Shape s : shapes) System.out.println(s);
        }
    }

    public static class Demo49 {
        static abstract class Employee {
            protected String name;
            Employee(String name) { this.name = name; }
            abstract double salary();
            void printInfo() { System.out.printf("%s 工资:%.2f%n", name, salary()); }
        }
        static class FullTime extends Employee {
            FullTime(String n) { super(n); }
            @Override double salary() { return 8000; }
        }
        static class PartTime extends Employee {
            private int hours;
            PartTime(String n, int h) { super(n); this.hours = h; }
            @Override double salary() { return hours * 50; }
        }
        public static void main(String[] args) {
            new FullTime("张三").printInfo();
            new PartTime("李四", 100).printInfo();
        }
    }

    public static class Demo50 {
        interface Flyable { int MAX_HEIGHT = 10000; void fly(); }
        interface Swimmable { void swim(); }
        static class Duck implements Flyable, Swimmable {
            @Override public void fly() { System.out.println("鸭子飞"); }
            @Override public void swim() { System.out.println("鸭子游泳"); }
        }
        public static void main(String[] args) {
            Duck d = new Duck();
            d.fly(); d.swim();
            System.out.println(Flyable.MAX_HEIGHT);
        }
    }

    public static class Demo51 {
        interface Vehicle {
            void run();
            default void horn() { System.out.println("滴滴"); }
            static Vehicle of(String type) {
                return () -> System.out.println(type + " 启动");
            }
        }
        public static void main(String[] args) {
            Vehicle v = Vehicle.of("汽车");
            v.run(); v.horn();
        }
    }

    public static class Demo52 {
        interface PayStrategy { void pay(double amount); }
        static class AliPay implements PayStrategy {
            @Override public void pay(double a) { System.out.println("支付宝支付 " + a); }
        }
        static class WeChatPay implements PayStrategy {
            @Override public void pay(double a) { System.out.println("微信支付 " + a); }
        }
        static class Order {
            private PayStrategy strategy;
            Order(PayStrategy s) { this.strategy = s; }
            void checkout(double amount) { strategy.pay(amount); }
        }
        public static void main(String[] args) {
            new Order(new AliPay()).checkout(99.9);
            new Order(new WeChatPay()).checkout(88.8);
        }
    }

    public static class Demo53 {
        static class A {}
        static class B extends A { void bMethod() { System.out.println("B 特有方法"); } }
        public static void main(String[] args) {
            A a = new B();
            if (a instanceof B b) b.bMethod();
            A real = new A();
            System.out.println(real instanceof B);
        }
    }

    public static class Demo54 {
        static final class Const { }
        static class Base { final void method() { System.out.println("final 方法"); } }
        static class Sub extends Base { final int MAX = 100; }
        public static void main(String[] args) {
            Sub s = new Sub();
            s.method();
            System.out.println(s.MAX);
        }
    }

    public static class Demo55 {
        static abstract class Beverage {
            final void prepare() {
                boilWater(); brew(); pourInCup();
                if (wantCondiment()) addCondiment();
            }
            void boilWater() { System.out.println("烧水"); }
            void pourInCup() { System.out.println("倒入杯中"); }
            abstract void brew();
            abstract void addCondiment();
            boolean wantCondiment() { return true; }
        }
        static class Coffee extends Beverage {
            @Override void brew() { System.out.println("冲泡咖啡"); }
            @Override void addCondiment() { System.out.println("加糖和奶"); }
        }
        public static void main(String[] args) { new Coffee().prepare(); }
    }

    // ============ 第6章 常用类(56-63) ============

    public static class Demo56 {
        public static void main(String[] args) {
            String s = "  Hello Java World  ";
            System.out.println("length:" + s.length());
            System.out.println("trim:" + s.trim());
            System.out.println("contains:" + s.contains("Java"));
            System.out.println("indexOf:" + s.indexOf("Java"));
            System.out.println("substring:" + s.substring(2, 7));
            System.out.println("replace:" + s.replace("Java", "Go"));
            System.out.println("split:" + String.join("|", "a,b,c".split(",")));
            System.out.println("equals:" + "abc".equalsIgnoreCase("ABC"));
            System.out.println("toUpperCase:" + s.trim().toUpperCase());
        }
    }

    public static class Demo57 {
        public static void main(String[] args) {
            long t1 = System.currentTimeMillis();
            String s = "";
            for (int i = 0; i < 10000; i++) s += i;
            long t2 = System.currentTimeMillis();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10000; i++) sb.append(i);
            long t3 = System.currentTimeMillis();
            System.out.println("String:" + (t2 - t1) + "ms");
            System.out.println("StringBuilder:" + (t3 - t2) + "ms");
            System.out.println(sb.reverse());
        }
    }

    public static class Demo58 {
        public static void main(String[] args) {
            Integer a = 127, b = 127;
            Integer c = 128, d = 128;
            System.out.println(a == b);
            System.out.println(c == d);
            System.out.println(c.equals(d));
            Integer x = Integer.valueOf("100");
            int y = x + 1;
            System.out.println(y);
            System.out.println(Integer.parseInt("42"));
        }
    }

    public static class Demo59 {
        public static void main(String[] args) {
            System.out.println("绝对值:" + Math.abs(-5));
            System.out.println("最大值:" + Math.max(3, 7));
            System.out.println("幂:" + Math.pow(2, 10));
            System.out.println("平方根:" + Math.sqrt(16));
            System.out.println("四舍五入:" + Math.round(3.567));
            System.out.println("向上取整:" + Math.ceil(3.1));
            System.out.println("向下取整:" + Math.floor(3.9));
            System.out.println("随机数:" + Math.random());
        }
    }

    public static class Demo60 {
        public static void main(String[] args) {
            Random r = new Random(42);
            System.out.println(r.nextInt(10));
            System.out.println(r.nextDouble());
            System.out.println(r.nextBoolean());
            SecureRandom sr = new SecureRandom();
            byte[] bytes = new byte[8];
            sr.nextBytes(bytes);
            for (byte b : bytes) System.out.printf("%02x", b);
            System.out.println();
        }
    }

    public static class Demo61 {
        public static void main(String[] args) {
            LocalDate today = LocalDate.now();
            LocalDate birth = LocalDate.of(2000, 5, 20);
            long days = ChronoUnit.DAYS.between(birth, today);
            System.out.println("出生至今:" + days + " 天");
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.println(now.format(fmt));
            LocalDateTime next = now.plusDays(7).plusHours(2);
            System.out.println("时长:" + Duration.between(now, next).toHours() + " 小时");
        }
    }

    public static class Demo62 {
        public static void main(String[] args) {
            String s = null;
            System.out.println(Objects.isNull(s));
            System.out.println(Objects.toString(s, "默认值"));
            System.out.println(Objects.equals("a", "a"));
            String[] arr = {"banana", "apple", "cherry"};
            Arrays.sort(arr, Comparator.comparingInt(String::length)
                    .thenComparing(Comparator.naturalOrder()));
            System.out.println(Arrays.toString(arr));
        }
    }

    public static class Demo63 {
        public static void main(String[] args) {
            String text = "邮箱:abc@test.com, 电话:13812345678";
            Pattern p1 = Pattern.compile("[\\w.]+@[\\w.]+");
            Matcher m1 = p1.matcher(text);
            while (m1.find()) System.out.println("邮箱:" + m1.group());
            Pattern p2 = Pattern.compile("1[3-9]\\d{9}");
            Matcher m2 = p2.matcher(text);
            while (m2.find()) System.out.println("手机:" + m2.group());
            System.out.println("纯数字校验:" + "123456".matches("\\d+"));
            System.out.println("替换:" + text.replaceAll("\\d", "*"));
        }
    }

    // ============ 第7章 集合框架(64-73) ============

    public static class Demo64 {
        public static void main(String[] args) {
            List<String> list = new ArrayList<>();
            list.add("Java"); list.add("Python"); list.add(1, "Go");
            System.out.println(list);
            System.out.println("get(0)=" + list.get(0));
            list.set(0, "C++"); list.remove("Python");
            System.out.println("size=" + list.size());
            System.out.println(list.contains("Go"));
            System.out.println(list);
        }
    }

    public static class Demo65 {
        public static void main(String[] args) {
            Deque<Integer> stack = new LinkedList<>();
            stack.push(1); stack.push(2); stack.push(3);
            System.out.println("栈顶:" + stack.peek());
            System.out.println("出栈:" + stack.pop());
            LinkedList<String> queue = new LinkedList<>();
            queue.offer("A"); queue.offer("B");
            System.out.println("队头:" + queue.peek());
            System.out.println("出队:" + queue.poll());
        }
    }

    public static class Demo66 {
        public static void main(String[] args) {
            Set<String> a = new HashSet<>(Set.of("Java", "C", "Go", "Java"));
            Set<String> b = new HashSet<>(Set.of("Go", "Rust", "C"));
            System.out.println("a=" + a + " (去重)");
            Set<String> union = new HashSet<>(a); union.addAll(b);
            Set<String> inter = new HashSet<>(a); inter.retainAll(b);
            Set<String> diff = new HashSet<>(a); diff.removeAll(b);
            System.out.println("并:" + union);
            System.out.println("交:" + inter);
            System.out.println("差:" + diff);
        }
    }

    public static class Demo67 {
        public static void main(String[] args) {
            TreeSet<Integer> set = new TreeSet<>();
            set.add(5); set.add(2); set.add(8); set.add(2);
            System.out.println("有序去重:" + set);
            System.out.println("first=" + set.first() + " last=" + set.last());
            System.out.println("headSet(5)=" + set.headSet(5));
            System.out.println("floor(4)=" + set.floor(4));
            TreeSet<String> byLen = new TreeSet<>(Comparator.comparingInt(String::length));
            byLen.addAll(List.of("abc", "a", "ab"));
            System.out.println("按长度:" + byLen);
        }
    }

    public static class Demo68 {
        public static void main(String[] args) {
            Map<String, Integer> map = new HashMap<>();
            map.put("苹果", 5); map.put("香蕉", 3);
            map.putIfAbsent("苹果", 100);
            map.merge("香蕉", 2, Integer::sum);
            System.out.println(map.get("苹果"));
            System.out.println(map.getOrDefault("西瓜", 0));
            System.out.println("containsKey:" + map.containsKey("香蕉"));
            map.remove("香蕉");
            for (Map.Entry<String, Integer> e : map.entrySet())
                System.out.println(e.getKey() + "=" + e.getValue());
        }
    }

    public static class Demo69 {
        public static void main(String[] args) {
            TreeMap<Integer, String> map = new TreeMap<>();
            map.put(3, "C"); map.put(1, "A"); map.put(5, "E"); map.put(2, "B");
            System.out.println("默认升序:" + map);
            System.out.println("firstKey:" + map.firstKey());
            System.out.println("headMap(3):" + map.headMap(3));
            System.out.println("tailMap(2):" + map.tailMap(2));
            System.out.println("降序:" + map.descendingMap());
        }
    }

    public static class Demo70 {
        public static void main(String[] args) {
            List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
            Iterator<Integer> it = list.iterator();
            while (it.hasNext()) if (it.next() % 2 == 0) it.remove();
            System.out.println("删除偶数后:" + list);
            list.removeIf(x -> x > 3);
            System.out.println("removeIf 后:" + list);
        }
    }

    public static class Demo71 {
        static class Pair<K, V> {
            private final K key; private final V value;
            Pair(K k, V v) { key = k; value = v; }
            public String toString() { return key + "=" + value; }
        }
        public static void main(String[] args) {
            List<Pair<String, Integer>> list = new ArrayList<>();
            list.add(new Pair<>("Java", 1995));
            list.add(new Pair<>("Go", 2009));
            for (Pair<String, Integer> p : list) System.out.println(p);
            List<? extends Number> nums = List.of(1, 2.5, 3L);
            double sum = 0;
            for (Number n : nums) sum += n.doubleValue();
            System.out.println("sum=" + sum);
        }
    }

    public static class Demo72 {
        public static void main(String[] args) {
            List<Integer> list = new ArrayList<>(List.of(3, 1, 4, 1, 5, 9, 2, 6));
            Collections.sort(list);
            System.out.println("排序:" + list);
            Collections.reverse(list);
            System.out.println("反转:" + list);
            System.out.println("max=" + Collections.max(list));
            System.out.println("min=" + Collections.min(list));
            Collections.swap(list, 0, 1);
            System.out.println("交换:" + list);
            System.out.println("二分查找4:" + Collections.binarySearch(
                    new ArrayList<>(List.of(1, 2, 3, 4, 5)), 4));
            System.out.println("不可变集合:" + Collections.unmodifiableList(list).size());
        }
    }

    public static class Demo73 {
        public static void main(String[] args) {
            String text = "the quick brown fox jumps over the lazy dog the fox";
            Map<String, Long> freq = Arrays.stream(text.split("\\s+"))
                    .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
            Map<String, Long> sorted = freq.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (a, b) -> a, LinkedHashMap::new));
            sorted.forEach((k, v) -> System.out.println(k + " -> " + v));
        }
    }

    // ============ 第8章 异常处理(74-77) ============

    public static class Demo74 {
        public static void main(String[] args) {
            try {
                int a = 10, b = 0;
                System.out.println(a / b);
            } catch (ArithmeticException e) {
                System.out.println("算术异常:" + e.getMessage());
            }
            System.out.println("程序继续执行");
        }
    }

    public static class Demo75 {
        static int divide(String a, String b) {
            try { return Integer.parseInt(a) / Integer.parseInt(b); }
            catch (NumberFormatException e) { System.out.println("格式错误:" + e.getMessage()); }
            catch (ArithmeticException e) { System.out.println("除零错误:" + e.getMessage()); }
            finally { System.out.println("finally 一定执行"); }
            return -1;
        }
        public static void main(String[] args) {
            System.out.println(divide("10", "2"));
            System.out.println(divide("abc", "2"));
            System.out.println(divide("10", "0"));
        }
    }

    public static class Demo76 {
        static class InsufficientBalanceException extends RuntimeException {
            private final double balance;
            public InsufficientBalanceException(double balance, String msg) {
                super(msg); this.balance = balance;
            }
            public double getBalance() { return balance; }
        }
        static class Wallet {
            private double balance;
            Wallet(double b) { balance = b; }
            void pay(double amount) {
                if (amount <= 0) throw new IllegalArgumentException("金额必须大于0");
                if (amount > balance)
                    throw new InsufficientBalanceException(balance, "余额不足,当前:" + balance);
                balance -= amount;
                System.out.println("支付成功,剩余:" + balance);
            }
        }
        public static void main(String[] args) {
            Wallet w = new Wallet(100);
            try { w.pay(150); }
            catch (InsufficientBalanceException e) {
                System.out.println("捕获:" + e.getMessage() + ", 余额=" + e.getBalance());
            } finally { w.pay(50); }
        }
    }

    public static class Demo77 {
        static class MyRes implements AutoCloseable {
            void doWork() { System.out.println("使用资源"); }
            @Override public void close() { System.out.println("资源已自动关闭"); }
        }
        public static void main(String[] args) {
            try (BufferedReader br = new BufferedReader(new FileReader("demo.txt"))) {
                String line;
                while ((line = br.readLine()) != null) System.out.println(line);
            } catch (IOException e) {
                System.out.println("IO异常:" + e.getMessage());
            }
            try (MyRes r = new MyRes()) { r.doWork(); }
        }
    }

    // ============ 第9章 IO 流(78-83) ============

    public static class Demo78 {
        public static void main(String[] args) throws IOException {
            File f = new File("test.txt");
            if (!f.exists()) System.out.println("创建文件:" + f.createNewFile());
            System.out.println("存在:" + f.exists());
            System.out.println("大小:" + f.length() + " 字节");
            System.out.println("绝对路径:" + f.getAbsolutePath());
            System.out.println("父目录:" + f.getParent());
            System.out.println("是否为文件:" + f.isFile());
            File dir = new File(".");
            String[] names = dir.list((d, n) -> n.endsWith(".java"));
            if (names != null) for (String n : names) System.out.println("java文件:" + n);
            System.out.println("删除:" + f.delete());
        }
    }

    public static class Demo79 {
        public static void main(String[] args) {
            try (FileInputStream in = new FileInputStream("src.txt");
                 FileOutputStream out = new FileOutputStream("dest.txt")) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) != -1) out.write(buf, 0, len);
                System.out.println("复制完成");
            } catch (IOException e) {
                System.out.println("复制失败:" + e.getMessage());
            }
        }
    }

    public static class Demo80 {
        public static void main(String[] args) {
            try (FileWriter fw = new FileWriter("note.txt")) {
                fw.write("第一行\n"); fw.write("第二行\n");
            } catch (IOException e) { e.printStackTrace(); }
            try (FileReader fr = new FileReader("note.txt")) {
                int c;
                while ((c = fr.read()) != -1) System.out.print((char) c);
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public static class Demo81 {
        public static void main(String[] args) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("buf.txt"));
                 BufferedReader br = new BufferedReader(new FileReader("buf.txt"))) {
                for (int i = 1; i <= 5; i++) { bw.write("第" + i + "行数据"); bw.newLine(); }
                bw.flush();
                String line;
                while ((line = br.readLine()) != null) System.out.println(line);
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public static class Demo82 {
        static class User implements Serializable {
            private static final long serialVersionUID = 1L;
            private String name;
            private transient int password;
            User(String name, int password) { this.name = name; this.password = password; }
            public String toString() { return "User(" + name + "," + password + ")"; }
        }
        public static void main(String[] args) throws Exception {
            User u = new User("张三", 123456);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("user.dat"))) {
                oos.writeObject(u);
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("user.dat"))) {
                User back = (User) ois.readObject();
                System.out.println("反序列化:" + back);
            }
        }
    }

    public static class Demo83 {
        public static void main(String[] args) {
            int lines = 0, chars = 0, words = 0;
            try (BufferedReader br = new BufferedReader(new FileReader("note.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    lines++;
                    chars += line.length();
                    words += line.trim().isEmpty() ? 0 : line.trim().split("\\s+").length;
                }
            } catch (IOException e) { e.printStackTrace(); }
            System.out.printf("行数:%d 字符数:%d 单词数:%d%n", lines, chars, words);
        }
    }

    // ============ 第10章 多线程(84-88) ============

    public static class Demo84 {
        static class MyThread extends Thread {
            MyThread(String name) { super(name); }
            @Override public void run() {
                for (int i = 1; i <= 3; i++) {
                    System.out.println(getName() + " 执行 " + i);
                    try { Thread.sleep(100); } catch (InterruptedException e) { return; }
                }
            }
        }
        public static void main(String[] args) {
            new MyThread("线程A").start();
            new MyThread("线程B").start();
        }
    }

    public static class Demo85 {
        public static void main(String[] args) throws Exception {
            Runnable r = () -> System.out.println("Runnable 线程:" + Thread.currentThread().getName());
            new Thread(r).start();
            Callable<Integer> task = () -> { Thread.sleep(200); return 1 + 2 + 3; };
            ExecutorService pool = Executors.newSingleThreadExecutor();
            Future<Integer> future = pool.submit(task);
            System.out.println("Callable 结果:" + future.get());
            pool.shutdown();
        }
    }

    public static class Demo86 {
        static class Ticket {
            private int count = 100;
            public synchronized boolean sell() {
                if (count <= 0) return false;
                count--;
                System.out.println(Thread.currentThread().getName() + " 卖出一张,剩余:" + count);
                return true;
            }
        }
        public static void main(String[] args) {
            Ticket t = new Ticket();
            Runnable task = () -> {
                while (t.sell()) {
                    try { Thread.sleep(5); } catch (InterruptedException e) { return; }
                }
            };
            for (int i = 0; i < 3; i++) new Thread(task, "窗口" + i).start();
        }
    }

    public static class Demo87 {
        public static void main(String[] args) throws InterruptedException {
            ExecutorService pool = Executors.newFixedThreadPool(3);
            for (int i = 1; i <= 8; i++) {
                int taskId = i;
                pool.submit(() -> {
                    System.out.println(Thread.currentThread().getName() + " 处理任务" + taskId);
                    try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                });
            }
            pool.shutdown();
            if (pool.awaitTermination(5, TimeUnit.SECONDS)) System.out.println("全部完成");
        }
    }

    public static class Demo88 {
        public static void main(String[] args) {
            BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(3);
            Thread producer = new Thread(() -> {
                try {
                    for (int i = 1; i <= 5; i++) { queue.put(i); System.out.println("生产:" + i); }
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
            Thread consumer = new Thread(() -> {
                try {
                    for (int i = 1; i <= 5; i++) {
                        Thread.sleep(150);
                        System.out.println("消费:" + queue.take());
                    }
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
            producer.start();
            consumer.start();
        }
    }

    // ============ 第11章 泛型与反射(89-91) ============

    public static class Demo89 {
        interface Mapper<T, R> { R map(T t); }
        static class Box<T extends Comparable<T>> {
            private T value;
            Box(T v) { value = v; }
            T get() { return value; }
            boolean greaterThan(T other) { return value.compareTo(other) > 0; }
        }
        static <T> T[] swap(T[] arr, int i, int j) {
            T t = arr[i]; arr[i] = arr[j]; arr[j] = t;
            return arr;
        }
        public static void main(String[] args) {
            Box<Integer> box = new Box<>(10);
            System.out.println(box.greaterThan(5));
            Mapper<String, Integer> len = String::length;
            System.out.println("长度:" + len.map("hello"));
            String[] arr = swap(new String[]{"a", "b", "c"}, 0, 2);
            System.out.println(Arrays.toString(arr));
        }
    }

    public static class Demo90 {
        public static void main(String[] args) throws Exception {
            Class<?> clazz = Class.forName("java.util.ArrayList");
            System.out.println("类名:" + clazz.getName());
            System.out.println("简单名:" + clazz.getSimpleName());
            System.out.println("父类:" + clazz.getSuperclass().getName());
            System.out.println("包:" + clazz.getPackageName());
            System.out.println("--- 构造器 ---");
            for (Constructor<?> c : clazz.getConstructors()) System.out.println(c);
            System.out.println("--- 部分方法 ---");
            Method[] methods = clazz.getMethods();
            for (int i = 0; i < 5 && i < methods.length; i++) System.out.println(methods[i].getName());
            System.out.println("--- 字段 ---");
            for (Field f : clazz.getDeclaredFields()) System.out.println(f);
        }
    }

    public static class Demo91 {
        static class Service {
            private String name = "默认";
            public String greet(String who) { return "Hello " + who + " from " + name; }
            private void secret() { System.out.println("私有方法被调用"); }
        }
        public static void main(String[] args) throws Exception {
            Class<?> clazz = Service.class;
            Object obj = clazz.getDeclaredConstructor().newInstance();
            var field = clazz.getDeclaredField("name");
            field.setAccessible(true);
            field.set(obj, "反射服务");
            Method greet = clazz.getMethod("greet", String.class);
            System.out.println(greet.invoke(obj, "张三"));
            Method secret = clazz.getDeclaredMethod("secret");
            secret.setAccessible(true);
            secret.invoke(obj);
        }
    }

    // ============ 第12章 枚举与注解(92-93) ============

    public static class Demo92 {
        enum Planet {
            MERCURY(3.303e+23, 2.4397e6),
            EARTH(5.976e+24, 6.37814e6),
            MARS(6.421e+23, 3.3972e6);
            private final double mass;
            private final double radius;
            Planet(double mass, double radius) { this.mass = mass; this.radius = radius; }
            double surfaceGravity() { return 6.67300E-11 * mass / (radius * radius); }
            @Override public String toString() {
                return name().charAt(0) + name().substring(1).toLowerCase();
            }
        }
        public static void main(String[] args) {
            for (Planet p : Planet.values())
                System.out.printf("%s 重力=%.2f%n", p, p.surfaceGravity());
            Planet e = Planet.valueOf("EARTH");
            System.out.println("ordinal=" + e.ordinal());
            System.out.println("比较:" + (e.compareTo(Planet.MARS) < 0));
        }
    }

    public static class Demo93 {
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.METHOD)
        @interface Info {
            String author() default "unknown";
            int version() default 1;
        }
        static class Api {
            @Info(author = "张三", version = 2)
            public void login() {}
            @Info
            public void logout() {}
        }
        public static void main(String[] args) throws Exception {
            Class<?> clazz = Api.class;
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Info.class)) {
                    Info info = m.getAnnotation(Info.class);
                    System.out.printf("%s -> author=%s, version=%d%n",
                            m.getName(), info.author(), info.version());
                }
            }
        }
    }

    // ============ 第13章 Lambda 与 Stream(94-96) ============

    public static class Demo94 {
        public static void main(String[] args) {
            Function<Integer, Integer> square = x -> x * x;
            System.out.println("平方:" + square.apply(5));
            Predicate<String> isEmpty = String::isEmpty;
            System.out.println("空判断:" + isEmpty.test(""));
            Supplier<Double> random = Math::random;
            System.out.println("随机:" + random.get());
            Consumer<String> printer = System.out::println;
            printer.accept("消费者输出");
            BiFunction<Integer, Integer, Integer> add = Integer::sum;
            System.out.println("相加:" + add.apply(3, 4));
            UnaryOperator<Integer> inc = x -> x + 1;
            System.out.println("自增:" + inc.apply(10));
        }
    }

    public static class Demo95 {
        public static void main(String[] args) {
            List<String> names = Arrays.asList("Tom", "Jerry", "Alice", "Bob", "Anna", "Tom");
            List<String> result = names.stream()
                    .filter(n -> n.length() > 3)
                    .map(String::toUpperCase)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            System.out.println(result);
            List<Integer> lengths = names.stream()
                    .map(String::length)
                    .collect(Collectors.toList());
            System.out.println("长度:" + lengths);
            System.out.println(names.stream().collect(Collectors.groupingBy(String::length)));
            System.out.println(names.stream().collect(Collectors.joining(", ", "[", "]")));
        }
    }

    public static class Demo96 {
        public static void main(String[] args) {
            List<Integer> nums = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5);
            int sum = nums.stream().reduce(0, Integer::sum);
            System.out.println("sum=" + sum);
            System.out.println("max=" + nums.stream().max(Integer::compareTo).orElse(-1));
            System.out.println("count=" + nums.stream().count());
            System.out.println("anyMatch(>8)=" + nums.stream().anyMatch(n -> n > 8));
            System.out.println("allMatch(>0)=" + nums.stream().allMatch(n -> n > 0));
            IntSummaryStatistics stat = nums.stream().mapToInt(Integer::intValue).summaryStatistics();
            System.out.printf("统计: count=%d, sum=%d, avg=%.2f, max=%d, min=%d%n",
                    stat.getCount(), stat.getSum(), stat.getAverage(), stat.getMax(), stat.getMin());
        }
    }

    // ============ 第14章 综合实战(97-100) ============

    public static class Demo97 {
        static class Server {
            public static void main(String[] args) throws Exception {
                try (ServerSocket server = new ServerSocket(8888)) {
                    System.out.println("服务端启动,端口 8888");
                    try (Socket socket = server.accept();
                         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                         PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                        String msg = in.readLine();
                        System.out.println("收到:" + msg);
                        out.println("服务端已收到:" + msg);
                    }
                }
            }
        }
        static class Client {
            public static void main(String[] args) throws Exception {
                try (Socket socket = new Socket("localhost", 8888);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    out.println("你好,服务端");
                    System.out.println("响应:" + in.readLine());
                }
            }
        }
        public static void main(String[] args) {
            System.out.println("Demo97 包含 Server 和 Client 两个 main,请分别运行:");
            System.out.println("  java Java100Examples$Demo97$Server");
            System.out.println("  java Java100Examples$Demo97$Client");
        }
    }

    public static class Demo98 {
        static final String URL = "jdbc:mysql://localhost:3306/test?serverTimezone=Asia/Shanghai";
        static final String USER = "root";
        static final String PWD = "123456";
        public static void main(String[] args) throws Exception {
            // 需要引入 mysql-connector-j 依赖
            try (Connection conn = DriverManager.getConnection(URL, USER, PWD);
                 Statement st = conn.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS user(" +
                        "id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(20), age INT)");
            }
            try (Connection conn = DriverManager.getConnection(URL, USER, PWD);
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO user(name, age) VALUES(?, ?)")) {
                ps.setString(1, "张三"); ps.setInt(2, 20); ps.addBatch();
                ps.setString(1, "李四"); ps.setInt(2, 22); ps.addBatch();
                ps.executeBatch();
            }
            List<String> list = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(URL, USER, PWD);
                 PreparedStatement ps = conn.prepareStatement("SELECT id, name, age FROM user");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(rs.getInt("id") + "-" + rs.getString("name") + "-" + rs.getInt("age"));
            }
            list.forEach(System.out::println);
        }
    }

    public static class Demo99 {
        static class Singleton {
            private static final Singleton INSTANCE = new Singleton();
            private Singleton() {}
            public static Singleton getInstance() { return INSTANCE; }
        }
        static class LazySingleton {
            private LazySingleton() {}
            private static class Holder { static final LazySingleton INSTANCE = new LazySingleton(); }
            public static LazySingleton getInstance() { return Holder.INSTANCE; }
        }
        interface Shape2 { double area(); }
        static class Circle2 implements Shape2 {
            private final double r; Circle2(double r) { this.r = r; }
            public double area() { return Math.PI * r * r; }
        }
        static class Rect2 implements Shape2 {
            private final double w, h; Rect2(double w, double h) { this.w = w; this.h = h; }
            public double area() { return w * h; }
        }
        static class ShapeFactory {
            public static Shape2 create(String type, double... p) {
                return switch (type.toLowerCase()) {
                    case "circle" -> new Circle2(p[0]);
                    case "rect" -> new Rect2(p[0], p[1]);
                    default -> throw new IllegalArgumentException("未知图形");
                };
            }
        }
        interface Observer { void update(String msg); }
        static class Subject {
            private final List<Observer> list = new ArrayList<>();
            void add(Observer o) { list.add(o); }
            void notifyAllObs(String msg) { list.forEach(o -> o.update(msg)); }
        }
        public static void main(String[] args) {
            System.out.println("单例:" + (Singleton.getInstance() == Singleton.getInstance()));
            System.out.println("懒单例:" + (LazySingleton.getInstance() == LazySingleton.getInstance()));
            System.out.printf("圆面积:%.2f%n", ShapeFactory.create("circle", 2).area());
            System.out.printf("矩形面积:%.2f%n", ShapeFactory.create("rect", 3, 4).area());
            Subject subject = new Subject();
            subject.add(msg -> System.out.println("订阅者A收到:" + msg));
            subject.add(msg -> System.out.println("订阅者B收到:" + msg));
            subject.notifyAllObs("系统更新通知");
        }
    }

    public static class Demo100 {
        static class BookItem {
            private final String isbn; private String title; private boolean borrowed;
            BookItem(String isbn, String title) { this.isbn = isbn; this.title = title; }
            String getIsbn() { return isbn; }
            String getTitle() { return title; }
            boolean isBorrowed() { return borrowed; }
            void setBorrowed(boolean b) { borrowed = b; }
            public String toString() {
                return isbn + "《" + title + "》" + (borrowed ? "[已借出]" : "[可借]");
            }
        }
        static class Library {
            private final Map<String, BookItem> books = new LinkedHashMap<>();
            private final Map<String, List<String>> borrowRecords = new HashMap<>();
            void addBook(String isbn, String title) {
                if (books.containsKey(isbn)) throw new IllegalArgumentException("ISBN重复:" + isbn);
                books.put(isbn, new BookItem(isbn, title));
            }
            void removeBook(String isbn) {
                BookItem b = books.get(isbn);
                if (b == null) throw new NoSuchElementException("图书不存在");
                if (b.isBorrowed()) throw new IllegalStateException("图书借出中,不可删除");
                books.remove(isbn);
            }
            void borrow(String user, String isbn) {
                BookItem b = requireBook(isbn);
                if (b.isBorrowed()) throw new IllegalStateException("已被借出:" + isbn);
                b.setBorrowed(true);
                borrowRecords.computeIfAbsent(user, k -> new ArrayList<>()).add(isbn);
            }
            void giveBack(String user, String isbn) {
                BookItem b = requireBook(isbn);
                List<String> list = borrowRecords.getOrDefault(user, new ArrayList<>());
                if (!list.contains(isbn)) throw new IllegalStateException("该用户未借此书");
                list.remove(isbn);
                b.setBorrowed(false);
            }
            private BookItem requireBook(String isbn) {
                BookItem b = books.get(isbn);
                if (b == null) throw new NoSuchElementException("图书不存在:" + isbn);
                return b;
            }
            List<BookItem> search(String keyword) {
                return books.values().stream()
                        .filter(b -> b.getTitle().contains(keyword) || b.getIsbn().contains(keyword))
                        .collect(Collectors.toList());
            }
            void printAll() { books.values().forEach(System.out::println); }
        }
        public static void main(String[] args) {
            Library lib = new Library();
            lib.addBook("B001", "Java 编程思想");
            lib.addBook("B002", "Effective Java");
            lib.addBook("B003", "算法导论");
            lib.borrow("张三", "B001");
            lib.borrow("李四", "B002");
            System.out.println("--- 全部图书 ---");
            lib.printAll();
            System.out.println("--- 搜索 'Java' ---");
            lib.search("Java").forEach(System.out::println);
            lib.giveBack("张三", "B001");
            System.out.println("--- 归还后 ---");
            lib.printAll();
            try { lib.borrow("王五", "B999"); }
            catch (NoSuchElementException e) { System.out.println("业务异常:" + e.getMessage()); }
        }
    }

    // ==================== 运行入口 ====================

    /**
     * 用法: java Java100Examples <n>   n 为 1~100
     *   例: java Java100Examples 50
     *   java Java100Examples            打印帮助
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("================ Java 100 Examples ================");
            System.out.println("用法: java Java100Examples <n>     运行第 n 题 (n=1..100)");
            System.out.println("示例: java Java100Examples 50");
            System.out.println("--------------------------------------------------");
            System.out.println("注意: 例 20/14 需控制台输入;");
            System.out.println("     例 78/79/80/81/82/83 需当前目录有对应文件;");
            System.out.println("     例 98 需 MySQL 驱动与数据库;");
            System.out.println("     例 97 请单独运行内部 Server / Client。");
            return;
        }
        int n;
        try { n = Integer.parseInt(args[0]); }
        catch (NumberFormatException e) { System.out.println("参数必须是数字 1~100"); return; }
        if (n < 1 || n > 100) { System.out.println("范围 1~100"); return; }

        Class<?>[] demos = {
                Demo01.class, Demo02.class, Demo03.class, Demo04.class, Demo05.class,
                Demo06.class, Demo07.class, Demo08.class, Demo09.class, Demo10.class,
                Demo11.class, Demo12.class, Demo13.class, Demo14.class, Demo15.class,
                Demo16.class, Demo17.class, Demo18.class, Demo19.class, Demo20.class,
                Demo21.class, Demo22.class, Demo23.class, Demo24.class, Demo25.class,
                Demo26.class, Demo27.class, Demo28.class, Demo29.class, Demo30.class,
                Demo31.class, Demo32.class, Demo33.class, Demo34.class, Demo35.class,
                Demo36.class, Demo37.class, Demo38.class, Demo39.class, Demo40.class,
                Demo41.class, Demo42.class, Demo43.class, Demo44.class, Demo45.class,
                Demo46.class, Demo47.class, Demo48.class, Demo49.class, Demo50.class,
                Demo51.class, Demo52.class, Demo53.class, Demo54.class, Demo55.class,
                Demo56.class, Demo57.class, Demo58.class, Demo59.class, Demo60.class,
                Demo61.class, Demo62.class, Demo63.class, Demo64.class, Demo65.class,
                Demo66.class, Demo67.class, Demo68.class, Demo69.class, Demo70.class,
                Demo71.class, Demo72.class, Demo73.class, Demo74.class, Demo75.class,
                Demo76.class, Demo77.class, Demo78.class, Demo79.class, Demo80.class,
                Demo81.class, Demo82.class, Demo83.class, Demo84.class, Demo85.class,
                Demo86.class, Demo87.class, Demo88.class, Demo89.class, Demo90.class,
                Demo91.class, Demo92.class, Demo93.class, Demo94.class, Demo95.class,
                Demo96.class, Demo97.class, Demo98.class, Demo99.class, Demo100.class
        };

        System.out.println(">>> 运行 Demo" + n + " <<<");
        demos[n - 1].getMethod("main", String[].class).invoke(null, (Object) new String[0]);
    }
}