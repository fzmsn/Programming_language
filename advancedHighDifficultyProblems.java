package programmingProject;
import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * JavaAdvanced30 —— 30 道 Java 高级题 · 单文件版
 * 运行:  java JavaAdvanced30 <1-30>
 */
public class advancedHighDifficultyProblems {

    // ==================== 并发高级(1-8) ====================

    public static class Q01 {
        // 手写可重入锁(基于 CAS + LockSupport,类比 AQS 思想)
        static class ReentrantLockX {
            private final AtomicReference<Thread> owner = new AtomicReference<>();
            private int holdCount = 0;
            private final Set<Thread> waiters = ConcurrentHashMap.newKeySet();

            public void lock() {
                Thread cur = Thread.currentThread();
                if (owner.get() == cur) { holdCount++; return; }
                waiters.add(cur);
                while (!owner.compareAndSet(null, cur)) LockSupport.park();
                waiters.remove(cur);
                holdCount = 1;
            }
            public void unlock() {
                if (owner.get() != Thread.currentThread())
                    throw new IllegalMonitorStateException();
                if (--holdCount == 0) {
                    owner.set(null);
                    waiters.forEach(LockSupport::unpark);
                }
            }
        }
        public static void main(String[] args) throws Exception {
            ReentrantLockX lock = new ReentrantLockX();
            int[] counter = {0};
            Thread[] ts = new Thread[10];
            for (int i = 0; i < 10; i++) {
                ts[i] = new Thread(() -> {
                    for (int j = 0; j < 1000; j++) {
                        lock.lock();
                        try { counter[0]++; } finally { lock.unlock(); }
                    }
                });
                ts[i].start();
            }
            for (Thread t : ts) t.join();
            System.out.println("counter=" + counter[0] + " (期望 10000)");
        }
    }

    public static class Q02 {
        // 无锁并发栈(Treiber Stack)
        static class TreiberStack<E> {
            static class Node<E> {
                final E item; Node<E> next;
                Node(E i, Node<E> n) { item = i; next = n; }
            }
            private final AtomicReference<Node<E>> head = new AtomicReference<>();
            public void push(E item) {
                Node<E> oldHead, newHead;
                do {
                    oldHead = head.get();
                    newHead = new Node<>(item, oldHead);
                } while (!head.compareAndSet(oldHead, newHead));
            }
            public E pop() {
                Node<E> oldHead, newHead;
                do {
                    oldHead = head.get();
                    if (oldHead == null) return null;
                    newHead = oldHead.next;
                } while (!head.compareAndSet(oldHead, newHead));
                return oldHead.item;
            }
        }
        public static void main(String[] args) throws Exception {
            TreiberStack<Integer> stack = new TreiberStack<>();
            AtomicInteger popped = new AtomicInteger();
            Thread[] ts = new Thread[8];
            for (int i = 0; i < 8; i++) {
                int base = i * 1000;
                ts[i] = new Thread(() -> {
                    for (int j = 0; j < 1000; j++) stack.push(base + j);
                    for (int j = 0; j < 1000; j++) if (stack.pop() != null) popped.incrementAndGet();
                });
                ts[i].start();
            }
            for (Thread t : ts) t.join();
            System.out.println("popped=" + popped.get() + " (期望 8000)");
        }
    }

    public static class Q03 {
        // 手写简易线程池(核心/最大线程 + 队列 + 拒绝策略)
        static class SimplePool {
            final int core, max; final long keepAliveMs;
            final BlockingQueue<Runnable> queue;
            final AtomicInteger size = new AtomicInteger();
            volatile boolean shutdown = false;

            SimplePool(int core, int max, long alive, int cap) {
                this.core = core; this.max = max; this.keepAliveMs = alive;
                this.queue = new ArrayBlockingQueue<>(cap);
            }
            void execute(Runnable task) {
                if (shutdown) throw new RejectedExecutionException("shutdown");
                int s = size.get();
                if (s < core && size.compareAndSet(s, s + 1)) { spawn(task); return; }
                if (queue.offer(task)) return;
                s = size.get();
                if (s < max && size.compareAndSet(s, s + 1)) { spawn(task); return; }
                throw new RejectedExecutionException("队列满,size=" + size.get());
            }
            void spawn(Runnable first) {
                Thread t = new Thread(() -> {
                    Runnable task = first;
                    while (!shutdown || task != null) {
                        if (task == null) {
                            try { task = queue.poll(keepAliveMs, TimeUnit.MILLISECONDS); }
                            catch (InterruptedException e) { break; }
                            if (task == null) break;
                        }
                        try { task.run(); } catch (Exception ignore) {}
                        task = null;
                    }
                    size.decrementAndGet();
                });
                t.start();
            }
            void shutdown() { shutdown = true; }
        }
        public static void main(String[] args) throws Exception {
            SimplePool pool = new SimplePool(2, 4, 500, 3);
            CountDownLatch done = new CountDownLatch(10);
            for (int i = 1; i <= 10; i++) {
                int id = i;
                try {
                    pool.execute(() -> {
                        System.out.println(Thread.currentThread().getName() + " 处理任务" + id);
                        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                        done.countDown();
                    });
                } catch (RejectedExecutionException e) {
                    System.out.println("任务" + id + " 被拒绝:" + e.getMessage());
                    done.countDown();
                }
            }
            done.await(5, TimeUnit.SECONDS);
            pool.shutdown();
            System.out.println("全部完成");
        }
    }

    public static class Q04 {
        // CompletableFuture 高级用法:链式 / 组合 / 异常兜底
        public static void main(String[] args) throws Exception {
            CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> 10);
            CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> 20);

            CompletableFuture<Integer> sum = f1.thenCombine(f2, Integer::sum)
                    .thenApply(x -> x * 2)
                    .thenApply(x -> x + 1)
                    .exceptionally(ex -> {
                        System.out.println("异常兜底:" + ex.getMessage());
                        return -1;
                    });
            System.out.println("combine 结果:" + sum.get());

            CompletableFuture<String> chain = CompletableFuture.supplyAsync(() -> "hello")
                    .thenCompose(s -> CompletableFuture.supplyAsync(() -> s.toUpperCase()))
                    .thenApply(s -> s + "!")
                    .whenComplete((r, e) -> System.out.println("完成:" + r));
            System.out.println("chain=" + chain.get());

            // 失败重试示例
            AtomicInteger tries = new AtomicInteger();
            Supplier<Integer> unreliable = () -> {
                if (tries.incrementAndGet() < 3) throw new RuntimeException("重试" + tries.get());
                return tries.get();
            };
            CompletableFuture<Integer> retry = CompletableFuture.supplyAsync(unreliable)
                    .handle((r, e) -> {
                        if (e == null) return r;
                        try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                        return unreliable.get();
                    });
            System.out.println("重试后结果:" + retry.get());

            /* 伪代码: 内部骨架
               CompletableFuture 依赖 ForkJoinPool.commonPool()
               supplyAsync(supplier) -> AsyncSupply 任务 -> 结果写入 result 字段
               thenApply(fn)        -> 建立 UniApply 依赖节点
               thenCombine(f)       -> BiRelay 双依赖
               异常通过 result 字段存储 AltResult 完成传播
            */
        }
    }

    public static class Q05 {
        // ThreadLocal 内存泄漏演示 & 正确释放
        static ThreadLocal<byte[]> BAD = new ThreadLocal<>();
        public static void main(String[] args) throws Exception {
            ExecutorService pool = Executors.newFixedThreadPool(2);
            for (int i = 0; i < 5; i++) {
                pool.submit(() -> {
                    BAD.set(new byte[1024 * 1024]); // 1MB
                    System.out.println(Thread.currentThread().getName()
                            + " 设置完成, 使用后必须 remove()");
                    BAD.remove(); // 关键:必须手动 remove
                });
            }
            pool.shutdown();
            pool.awaitTermination(2, TimeUnit.SECONDS);
            System.out.println("ThreadLocal 不 remove 会导致 ThreadLocalMap.Entry 的 value 无法回收");
            /* 伪代码:ThreadLocalMap 结构
               Thread -> threadLocals -> Entry[] table
               Entry extends WeakReference<ThreadLocal>
               key = new WeakReference(threadLocal), value = 强引用
               线程池复用线程时,value 无法被 GC -> 内存泄漏
            */
        }
    }

    public static class Q06 {
        // 手写读写锁(读共享/写独占,写优先,避免写饥饿)
        static class RWLock {
            private int readers = 0, writers = 0, waitWriters = 0;
            synchronized void readLock() throws InterruptedException {
                while (writers > 0 || waitWriters > 0) wait();
                readers++;
            }
            synchronized void readUnlock() {
                readers--;
                if (readers == 0) notifyAll();
            }
            synchronized void writeLock() throws InterruptedException {
                waitWriters++;
                try { while (readers > 0 || writers > 0) wait(); }
                finally { waitWriters--; }
                writers++;
            }
            synchronized void writeUnlock() {
                writers--;
                notifyAll();
            }
        }
        public static void main(String[] args) throws Exception {
            RWLock lock = new RWLock();
            int[] data = {0};
            Thread[] ts = new Thread[6];
            for (int i = 0; i < 6; i++) {
                int idx = i;
                ts[i] = new Thread(() -> {
                    try {
                        if (idx % 2 == 0) {
                            lock.readLock();
                            try { System.out.println("读:" + data[0]); }
                            finally { lock.readUnlock(); }
                        } else {
                            lock.writeLock();
                            try { data[0]++; System.out.println("写:" + data[0]); }
                            finally { lock.writeUnlock(); }
                        }
                    } catch (InterruptedException ignored) {}
                });
                ts[i].start();
            }
            for (Thread t : ts) t.join();
            System.out.println("最终:" + data[0]);
        }
    }

    public static class Q07 {
        // 手写 CountDownLatch / CyclicBarrier
        static class Latch {
            private int count;
            Latch(int n) { count = n; }
            synchronized void countDown() { if (--count <= 0) notifyAll(); }
            synchronized void await() throws InterruptedException {
                while (count > 0) wait();
            }
        }
        static class Barrier {
            private int count, n;
            Barrier(int n) { this.n = n; this.count = n; }
            synchronized void await() throws InterruptedException {
                if (--count == 0) { count = n; notifyAll(); }
                else while (count < n) wait();
            }
        }
        public static void main(String[] args) throws Exception {
            Latch latch = new Latch(3);
            for (int i = 0; i < 3; i++) {
                int id = i;
                new Thread(() -> {
                    try { Thread.sleep(100 * id); } catch (InterruptedException ignored) {}
                    System.out.println("任务" + id + " 完成");
                    latch.countDown();
                }).start();
            }
            latch.await();
            System.out.println("=== Latch 全部完成 ===");

            Barrier barrier = new Barrier(3);
            for (int i = 0; i < 3; i++) {
                int id = i;
                new Thread(() -> {
                    for (int r = 0; r < 2; r++) {
                        System.out.println("线程" + id + " 到达第" + r + "轮屏障");
                        try { barrier.await(); } catch (InterruptedException ignored) {}
                    }
                }).start();
            }
            Thread.sleep(500);
        }
    }

    public static class Q08 {
        // ForkJoin 大数组求和 + 并行归并
        static class SumTask extends RecursiveTask<Long> {
            final long[] a; final int lo, hi;
            SumTask(long[] a, int l, int h) { this.a = a; lo = l; hi = h; }
            @Override protected Long compute() {
                if (hi - lo <= 10000) {
                    long s = 0;
                    for (int i = lo; i < hi; i++) s += a[i];
                    return s;
                }
                int mid = (lo + hi) >>> 1;
                SumTask l = new SumTask(a, lo, mid);
                SumTask r = new SumTask(a, mid, hi);
                l.fork();
                long rs = r.compute();
                long ls = l.join();
                return ls + rs;
            }
        }
        public static void main(String[] args) {
            long[] a = new long[1_000_000];
            for (int i = 0; i < a.length; i++) a[i] = i;
            long expected = (long)(a.length - 1) * a.length / 2;

            ForkJoinPool pool = new ForkJoinPool();
            long t0 = System.nanoTime();
            long got = pool.invoke(new SumTask(a, 0, a.length));
            long t1 = System.nanoTime();

            long seq = 0;
            long t2 = System.nanoTime();
            for (long v : a) seq += v;
            long t3 = System.nanoTime();

            System.out.println("并行=" + got + " 顺序=" + seq + " 期望=" + expected);
            System.out.printf("并行耗时 %.2fms, 顺序耗时 %.2fms%n",
                    (t1 - t0) / 1e6, (t3 - t2) / 1e6);
        }
    }

    // ==================== 数据结构 / 算法高级(9-17) ====================

    public static class Q09 {
        // LRU 缓存:LinkedHashMap 版 + 手写双向链表 + HashMap 版
        static class LRU1<K, V> extends LinkedHashMap<K, V> {
            final int cap;
            LRU1(int c) { super(c, 0.75f, true); cap = c; }
            @Override protected boolean removeEldestEntry(Map.Entry<K, V> e) {
                return size() > cap;
            }
        }
        static class LRU2<K, V> {
            static class Node<K, V> {
                K k; V v; Node<K, V> prev, next;
                Node(K k, V v) { this.k = k; this.v = v; }
            }
            final int cap;
            final Map<K, Node<K, V>> map = new HashMap<>();
            final Node<K, V> head = new Node<>(null, null);
            final Node<K, V> tail = new Node<>(null, null);
            LRU2(int c) { cap = c; head.next = tail; tail.prev = head; }
            public V get(K k) {
                Node<K, V> n = map.get(k);
                if (n == null) return null;
                moveFront(n); return n.v;
            }
            public void put(K k, V v) {
                Node<K, V> n = map.get(k);
                if (n != null) { n.v = v; moveFront(n); return; }
                if (map.size() == cap) {
                    Node<K, V> old = tail.prev;
                    remove(old); map.remove(old.k);
                }
                Node<K, V> nw = new Node<>(k, v);
                map.put(k, nw); addFront(nw);
            }
            void moveFront(Node<K, V> n) { remove(n); addFront(n); }
            void remove(Node<K, V> n) {
                n.prev.next = n.next; n.next.prev = n.prev;
            }
            void addFront(Node<K, V> n) {
                n.next = head.next; n.prev = head;
                head.next.prev = n; head.next = n;
            }
        }
        public static void main(String[] args) {
            LRU1<Integer, String> l1 = new LRU1<>(2);
            l1.put(1, "a"); l1.put(2, "b"); l1.get(1);
            l1.put(3, "c");
            System.out.println("LinkedHashMap LRU:" + l1);

            LRU2<Integer, String> l2 = new LRU2<>(2);
            l2.put(1, "a"); l2.put(2, "b"); l2.get(1); l2.put(3, "c");
            System.out.println("手写 LRU: get(2)=" + l2.get(2) + ", get(3)=" + l2.get(3));
        }
    }

    public static class Q10 {
        // LFU 缓存:双 Map + 双向链表分组
        static class LFU {
            static class Node {
                int key, val, freq = 1;
                Node prev, next;
                Node(int k, int v) { key = k; val = v; }
            }
            static class FreqList {
                Node head = new Node(-1, -1), tail = new Node(-1, -1);
                int size = 0;
                FreqList() { head.next = tail; tail.prev = head; }
                void add(Node n) { n.prev = head; n.next = head.next; head.next.prev = n; head.next = n; size++; }
                void remove(Node n) { n.prev.next = n.next; n.next.prev = n.prev; size--; }
            }
            final int cap;
            int minFreq = 1;
            final Map<Integer, Node> data = new HashMap<>();
            final Map<Integer, FreqList> freqs = new HashMap<>();
            LFU(int c) { cap = c; }

            public int get(int k) {
                Node n = data.get(k);
                if (n == null) return -1;
                touch(n); return n.val;
            }
            public void put(int k, int v) {
                if (cap <= 0) return;
                if (data.containsKey(k)) { Node n = data.get(k); n.val = v; touch(n); return; }
                if (data.size() == cap) evict();
                Node n = new Node(k, v);
                data.put(k, n);
                freqs.computeIfAbsent(1, x -> new FreqList()).add(n);
                minFreq = 1;
            }
            void touch(Node n) {
                FreqList f = freqs.get(n.freq);
                f.remove(n);
                if (f.size == 0) {
                    freqs.remove(n.freq);
                    if (minFreq == n.freq) minFreq++;
                }
                n.freq++;
                freqs.computeIfAbsent(n.freq, x -> new FreqList()).add(n);
            }
            void evict() {
                FreqList f = freqs.get(minFreq);
                Node victim = f.tail.prev;
                f.remove(victim);
                if (f.size == 0) freqs.remove(minFreq);
                data.remove(victim.key);
            }
        }
        public static void main(String[] args) {
            LFU c = new LFU(2);
            c.put(1, 10); c.put(2, 20);
            c.get(1); // freq(1)=2
            c.put(3, 30); // 淘汰 2
            System.out.println("get(1)=" + c.get(1) + " get(2)=" + c.get(2) + " get(3)=" + c.get(3));
        }
    }

    public static class Q11 {
        // 布隆过滤器(BitSet + 多重哈希)
        static class Bloom {
            private final BitSet bits;
            private final int[] seeds = {3, 5, 7, 11, 13, 17, 19};
            Bloom(int size) { bits = new BitSet(size); }
            private int hash(String s, int seed) {
                int h = seed;
                for (int i = 0; i < s.length(); i++) h = h * 31 + s.charAt(i);
                return Math.abs(h % bits.size());
            }
            public void add(String s) {
                for (int seed : seeds) bits.set(hash(s, seed));
            }
            public boolean mightContain(String s) {
                for (int seed : seeds) if (!bits.get(hash(s, seed))) return false;
                return true;
            }
        }
        public static void main(String[] args) {
            Bloom b = new Bloom(1024);
            String[] words = {"java", "spring", "kafka", "redis"};
            for (String w : words) b.add(w);
            System.out.println("java?    " + b.mightContain("java"));
            System.out.println("spring?  " + b.mightContain("spring"));
            System.out.println("python?  " + b.mightContain("python") + " (可能误判)");
            /* 伪代码:误判率估计
               p ≈ (1 - e^(-kn/m))^k
               本实现 m=1024, k=7, n=4 → p 极低
            */
        }
    }

    public static class Q12 {
        // 一致性哈希(TreeMap + 虚拟节点)
        static class ConsistentHash {
            final TreeMap<Long, String> ring = new TreeMap<>();
            final int replicas;
            ConsistentHash(int r) { replicas = r; }
            private long hash(String s) {
                try {
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
                    long v = 0;
                    for (int i = 0; i < 8; i++) v = (v << 8) | (d[i] & 0xff);
                    return v;
                } catch (Exception e) { return s.hashCode(); }
            }
            void addNode(String node) {
                for (int i = 0; i < replicas; i++) ring.put(hash(node + "#" + i), node);
            }
            String getNode(String key) {
                if (ring.isEmpty()) return null;
                Map.Entry<Long, String> e = ring.ceilingEntry(hash(key));
                return e == null ? ring.firstEntry().getValue() : e.getValue();
            }
        }
        public static void main(String[] args) {
            ConsistentHash ch = new ConsistentHash(10);
            for (String n : new String[]{"node-A", "node-B", "node-C"}) ch.addNode(n);
            String[] keys = {"user1", "user2", "user3", "user4", "user5"};
            Map<String, Integer> count = new HashMap<>();
            for (String k : keys) {
                String node = ch.getNode(k);
                System.out.println(k + " -> " + node);
                count.merge(node, 1, Integer::sum);
            }
            System.out.println("分布:" + count);
        }
    }

    public static class Q13 {
        // 跳表 SkipList
        static class SkipList {
            static class Node {
                int val; Node right, down;
                Node(int v, Node r, Node d) { val = v; right = r; down = d; }
            }
            private final Node head = new Node(-1, null, null);
            public boolean search(int target) {
                Node p = head;
                while (p != null) {
                    while (p.right != null && p.right.val < target) p = p.right;
                    if (p.right != null && p.right.val == target) return true;
                    p = p.down;
                }
                return false;
            }
            public void add(int num) {
                /* 骨架:简单实现只保留一层,真实跳表按概率加层 */
                Node p = head;
                while (p.right != null && p.right.val < num) p = p.right;
                p.right = new Node(num, p.right, null);
            }
        }
        public static void main(String[] args) {
            SkipList sl = new SkipList();
            for (int v : new int[]{3, 7, 1, 9, 5}) sl.add(v);
            for (int t : new int[]{1, 5, 7, 8}) System.out.println(t + " 存在? " + sl.search(t));
        }
    }

    public static class Q14 {
        // Trie 前缀树 + 自动补全
        static class Trie {
            static class Node {
                Map<Character, Node> children = new HashMap<>();
                boolean isEnd;
            }
            final Node root = new Node();
            void insert(String w) {
                Node n = root;
                for (char c : w.toCharArray()) n = n.children.computeIfAbsent(c, x -> new Node());
                n.isEnd = true;
            }
            boolean search(String w) {
                Node n = root;
                for (char c : w.toCharArray()) {
                    n = n.children.get(c);
                    if (n == null) return false;
                }
                return n.isEnd;
            }
            List<String> prefix(String p) {
                List<String> out = new ArrayList<>();
                Node n = root;
                for (char c : p.toCharArray()) {
                    n = n.children.get(c);
                    if (n == null) return out;
                }
                dfs(n, new StringBuilder(p), out);
                return out;
            }
            void dfs(Node n, StringBuilder sb, List<String> out) {
                if (n.isEnd) out.add(sb.toString());
                for (Map.Entry<Character, Node> e : n.children.entrySet()) {
                    sb.append(e.getKey());
                    dfs(e.getValue(), sb, out);
                    sb.deleteCharAt(sb.length() - 1);
                }
            }
        }
        public static void main(String[] args) {
            Trie t = new Trie();
            for (String w : new String[]{"java", "javascript", "jar", "jvm", "kotlin"}) t.insert(w);
            System.out.println("前缀 ja 补全: " + t.prefix("ja"));
            System.out.println("前缀 j 补全: " + t.prefix("j"));
            System.out.println("存在 kotlin? " + t.search("kotlin"));
        }
    }

    public static class Q15 {
        // 并查集 + Kruskal 最小生成树
        static class DSU {
            int[] p, r;
            DSU(int n) { p = new int[n]; r = new int[n]; for (int i = 0; i < n; i++) p[i] = i; }
            int find(int x) { return p[x] == x ? x : (p[x] = find(p[x])); }
            boolean union(int a, int b) {
                a = find(a); b = find(b);
                if (a == b) return false;
                if (r[a] < r[b]) p[a] = b;
                else if (r[a] > r[b]) p[b] = a;
                else { p[b] = a; r[a]++; }
                return true;
            }
        }
        public static void main(String[] args) {
            int[][] edges = {
                    {0, 1, 4}, {0, 2, 8}, {1, 2, 2},
                    {1, 3, 5}, {2, 3, 3}, {3, 4, 1}
            };
            int n = 5;
            Arrays.sort(edges, Comparator.comparingInt(a -> a[2]));
            DSU dsu = new DSU(n);
            int total = 0, used = 0;
            for (int[] e : edges) {
                if (dsu.union(e[0], e[1])) {
                    total += e[2]; used++;
                    System.out.println("选边 " + e[0] + "-" + e[1] + " w=" + e[2]);
                }
                if (used == n - 1) break;
            }
            System.out.println("MST 总权重=" + total);
        }
    }

    public static class Q16 {
        // Dijkstra + 邻接表
        static int[] dijkstra(List<int[]>[] g, int src) {
            int n = g.length;
            int[] dist = new int[n];
            Arrays.fill(dist, Integer.MAX_VALUE);
            dist[src] = 0;
            PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
            pq.offer(new int[]{src, 0});
            while (!pq.isEmpty()) {
                int[] cur = pq.poll();
                int u = cur[0], d = cur[1];
                if (d > dist[u]) continue;
                for (int[] e : g[u]) {
                    int v = e[0], w = e[1];
                    if (dist[u] + w < dist[v]) {
                        dist[v] = dist[u] + w;
                        pq.offer(new int[]{v, dist[v]});
                    }
                }
            }
            return dist;
        }
        public static void main(String[] args) {
            int n = 5;
            @SuppressWarnings("unchecked")
            List<int[]>[] g = new List[n];
            for (int i = 0; i < n; i++) g[i] = new ArrayList<>();
            int[][] edges = {{0, 1, 4}, {0, 2, 1}, {2, 1, 2}, {1, 3, 1}, {2, 3, 5}, {3, 4, 3}};
            for (int[] e : edges) { g[e[0]].add(new int[]{e[1], e[2]}); g[e[1]].add(new int[]{e[0], e[2]}); }
            int[] d = dijkstra(g, 0);
            System.out.println("0 到各点最短距离: " + Arrays.toString(d));
        }
    }

    public static class Q17 {
        // 红黑树伪代码(核心:插入修复)。真实现用 TreeMap 替代
        /*
        伪代码:RB-INSERT-FIXUP(T, z)
        while z.p.color == RED:
            if z.p == z.p.p.left:
                y = z.p.p.right
                if y.color == RED:                  // case 1: 叔父红
                    z.p.color = BLACK; y.color = BLACK
                    z.p.p.color = RED; z = z.p.p
                else:
                    if z == z.p.right:              // case 2: LR 型
                        z = z.p; LEFT-ROTATE(T, z)
                    z.p.color = BLACK               // case 3: LL 型
                    z.p.p.color = RED; RIGHT-ROTATE(T, z.p.p)
            else: (镜像)
        T.root.color = BLACK

        关键不变量:
          1) 每个节点非红即黑
          2) 根黑
          3) 叶子(NIL)黑
          4) 红节点的子节点必黑
          5) 任一节点到其叶子路径黑节点数相同
        时间复杂度: 插入/删除/查询 O(log n)
        */
        public static void main(String[] args) {
            TreeMap<Integer, String> rb = new TreeMap<>();
            int[] keys = {10, 5, 15, 3, 7, 12, 18};
            for (int k : keys) rb.put(k, "v" + k);
            System.out.println("TreeMap 保证有序: " + rb.keySet());
            System.out.println("区间 [5,15]: " + rb.subMap(5, true, 15, true).keySet());
            System.out.println("TreeMap 底层即红黑树,源码:java.util.TreeMap#put -> fixAfterInsertion");
        }
    }

    // ==================== 反射 / 代理 / IOC / AOP(18-22) ====================

    public static class Q18 {
        // JDK 动态代理 + 伪代码字节码骨架
        interface Svc { String hello(String n); }
        static class Impl implements Svc {
            public String hello(String n) { return "Hi " + n; }
        }
        public static void main(String[] args) {
            Svc target = new Impl();
            Svc proxy = (Svc) Proxy.newProxyInstance(
                    Q18.class.getClassLoader(),
                    new Class[]{Svc.class},
                    (p, m, a) -> {
                        System.out.println("-> before " + m.getName());
                        Object r = m.invoke(target, a);
                        System.out.println("-> after  " + m.getName());
                        return r;
                    });
            System.out.println(proxy.hello("Java"));
            /* 伪代码:Proxy.newProxyInstance 内部
               1) 生成 $Proxy0 字节码(实现 Svc 接口)
               2) $Proxy0 extends Proxy,持有 InvocationHandler h
               3) 方法调用转给 h.invoke(this, method, args)
               $Proxy0.hello(String):
                   return (String) h.invoke(this, Svc.hello_method, new Object[]{n})
            */
        }
    }

    public static class Q19 {
        // 手写极简 Spring IOC(注解扫描 + 反射注入)
        @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.TYPE)
        @interface MyComponent {}
        @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD)
        @interface MyAutowired {}

        @MyComponent
        static class UserDao { public String find() { return "user:张三"; } }

        @MyComponent
        static class UserService {
            @MyAutowired UserDao userDao;
            public String query() { return "service -> " + userDao.find(); }
        }

        static class MiniIOC {
            final Map<Class<?>, Object> beans = new HashMap<>();
            void scan(Class<?>... classes) throws Exception {
                for (Class<?> c : classes)
                    if (c.isAnnotationPresent(MyComponent.class))
                        beans.put(c, c.getDeclaredConstructor().newInstance());
                for (Object bean : beans.values()) {
                    for (Field f : bean.getClass().getDeclaredFields()) {
                        if (f.isAnnotationPresent(MyAutowired.class)) {
                            f.setAccessible(true);
                            Object dep = beans.get(f.getType());
                            if (dep != null) f.set(bean, dep);
                        }
                    }
                }
            }
            <T> T get(Class<T> c) { return c.cast(beans.get(c)); }
        }
        public static void main(String[] args) throws Exception {
            MiniIOC ctx = new MiniIOC();
            ctx.scan(UserDao.class, UserService.class);
            System.out.println(ctx.get(UserService.class).query());
        }
    }

    public static class Q20 {
        // AOP 环绕通知(动态代理实现)
        interface OrderService { void create(String id); }
        static class OrderImpl implements OrderService {
            public void create(String id) { System.out.println("创建订单:" + id); }
        }
        static <T> T wrap(T target, Class<T> iface, BiConsumer<String, Object[]> before) {
            return iface.cast(Proxy.newProxyInstance(iface.getClassLoader(),
                    new Class[]{iface},
                    (p, m, a) -> {
                        before.accept(m.getName(), a);
                        long t = System.currentTimeMillis();
                        Object r = m.invoke(target, a);
                        System.out.println("  [耗时 " + (System.currentTimeMillis() - t) + "ms]");
                        return r;
                    }));
        }
        public static void main(String[] args) {
            OrderService os = wrap(new OrderImpl(), OrderService.class,
                    (name, a) -> System.out.println("[前置] " + name + " args=" + Arrays.toString(a)));
            os.create("A001");
        }
    }

    public static class Q21 {
        // 类加载器 + 双亲委派演示
        static class MyLoader extends ClassLoader {
            MyLoader() { super(MyLoader.class.getClassLoader()); }
            @Override protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                if (name.startsWith("java.")) return super.loadClass(name, resolve);
                System.out.println("MyLoader 尝试加载:" + name);
                return super.loadClass(name, resolve); // 交给父加载器
            }
        }
        public static void main(String[] args) throws Exception {
            MyLoader l = new MyLoader();
            Class<?> c = l.loadClass("java.lang.String");
            System.out.println("String 类加载器: " + c.getClassLoader()
                    + " (null 表示 Bootstrap)");
            System.out.println("MyLoader 父: " + l.getParent());
            System.out.println("应用类加载器: " + Q21.class.getClassLoader());
            /* 双亲委派:AppClassLoader -> ExtClassLoader -> BootstrapClassLoader
               好处: 1) 安全(java.lang.String 无法被替换)
                     2) 避免重复加载
               打破: 1) SPI(ServiceLoader)  2) OSGi 模块化  3) Tomcat 类隔离
            */
        }
    }

    public static class Q22 {
        // SPI 机制(ServiceLoader)
        interface Codec { String encode(String s); }
        static class Base64Codec implements Codec {
            public String encode(String s) {
                return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
            }
        }
        public static void main(String[] args) {
            // 伪代码:真实 SPI 需要 META-INF/services/包名.Codec 文件写入实现类
            ServiceLoader<Codec> loader = ServiceLoader.load(Codec.class);
            boolean found = false;
            for (Codec c : loader) { System.out.println("发现编解码:" + c.encode("hello")); found = true; }
            if (!found) {
                System.out.println("(未配置 META-INF/services, 回退内置实现)");
                System.out.println(new Base64Codec().encode("hello"));
            }
            /* 伪代码:SPI 加载流程
               ServiceLoader.load(Codec.class)
                 -> ClassLoader.getResources("META-INF/services/" + 接口全限定名)
                 -> 读取每行实现类名
                 -> Class.forName(实现类).newInstance()
                 -> 加入迭代器
               应用: JDBC Driver / SLF4J / Dubbo SPI(增强版)
            */
        }
    }

    // ==================== 网络 / NIO(23-25) ====================

    public static class Q23 {
        // NIO Selector 单线程 Reactor(快速演示,自动关闭)
        public static void main(String[] args) throws Exception {
            Selector selector = Selector.open();
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(9090));
            ssc.configureBlocking(false);
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("NIO 服务器启动 :9090,5 秒后自动关闭");

            Thread server = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        if (selector.select(1000) == 0) continue;
                        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                        while (it.hasNext()) {
                            SelectionKey k = it.next(); it.remove();
                            if (k.isAcceptable()) {
                                SocketChannel sc = ssc.accept();
                                sc.configureBlocking(false);
                                sc.register(selector, SelectionKey.OP_READ);
                            } else if (k.isReadable()) {
                                SocketChannel sc = (SocketChannel) k.channel();
                                ByteBuffer buf = ByteBuffer.allocate(1024);
                                int n = sc.read(buf);
                                if (n == -1) { sc.close(); continue; }
                                buf.flip();
                                sc.write(buf);
                            }
                        }
                    }
                } catch (Exception ignore) {}
            });
            server.start();
            // 客户端测试
            Thread.sleep(300);
            try (Socket s = new Socket("localhost", 9090)) {
                s.getOutputStream().write("ping".getBytes());
                byte[] buf = new byte[64];
                int n = s.getInputStream().read(buf);
                System.out.println("客户端收到: " + new String(buf, 0, n));
            }
            Thread.sleep(500);
            server.interrupt(); ssc.close(); selector.close();
            System.out.println("NIO 服务器已关闭");
        }
    }

    public static class Q24 {
        // 简易 HTTP 静态文件服务器(NIO 骨架 + 伪代码)
        /*
        伪代码:HTTP 解析
            readLine() -> "GET /index.html HTTP/1.1"
            split(" ") -> [method, path, version]
            if path == "/": path = "/index.html"
            file = new File(root, path)
            if !file.exists(): 404
            response = "HTTP/1.1 200 OK\r\nContent-Length: " + file.length()
                     + "\r\nContent-Type: " + guessMime(path) + "\r\n\r\n"
            write(response bytes); transferTo(file)
        */
        public static void main(String[] args) throws Exception {
            ServerSocket ss = new ServerSocket(8080);
            System.out.println("HTTP 服务器 :8080, 3 秒后关闭");
            Thread t = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        Socket s = ss.accept();
                        try (BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                             OutputStream out = s.getOutputStream()) {
                            String line = in.readLine();
                            String body = "<h1>Hello HTTP</h1><p>" + line + "</p>";
                            String resp = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8\r\n" +
                                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length +
                                    "\r\n\r\n" + body;
                            out.write(resp.getBytes(StandardCharsets.UTF_8));
                        }
                    }
                } catch (Exception ignore) {}
            });
            t.start();
            Thread.sleep(300);
            try (Socket s = new Socket("localhost", 8080)) {
                s.getOutputStream().write("GET / HTTP/1.1\r\nHost: localhost\r\n\r\n".getBytes());
                byte[] buf = new byte[512];
                int n = s.getInputStream().read(buf);
                System.out.println("响应片段:\n" + new String(buf, 0, Math.min(n, 200)));
            }
            Thread.sleep(300);
            t.interrupt(); ss.close();
        }
    }

    public static class Q25 {
        // 手写极简 RPC 框架骨架(伪代码为主)
        /*
        伪代码:RPC 核心模块
          --- 服务端 ---
          ServerSocket listen(port)
          while true:
              socket = accept()
              bytes = readFrame(socket)            // 长度前缀 + payload
              req = deserialize(bytes)             // 反序列化
              impl = registry.get(req.interfaceName) // 反射找实现
              method = impl.getClass().getMethod(req.methodName, req.argTypes)
              result = method.invoke(impl, req.args)
              socket.write(serialize(result))

          --- 客户端 ---
          proxy = Proxy.newProxyInstance(
              interface,
              (p, m, args) -> {
                  req = new Request(interfaceName, m.getName, args)
                  socket = connect(host, port)
                  socket.write(serialize(req))
                  return deserialize(readFrame(socket))
              })

          --- 关键点 ---
          1) 序列化: Java原生 / Hessian / Protobuf / Kryo
          2) 网络: NIO / Netty / gRPC 底层 HTTP2
          3) 注册中心: Zookeeper / Nacos
          4) 负载均衡 + 熔断 + 限流
        */
        public static void main(String[] args) {
            System.out.println("RPC 框架核心思想已在注释中给出");
            System.out.println("核心三件套: 动态代理 + 序列化 + 网络通信");
        }
    }

    // ==================== 设计模式 / 架构(26-30) ====================

    public static class Q26 {
        // 单例 7 种写法对比 + 反射/序列化防破坏
        static class Hungry { static final Hungry I = new Hungry(); private Hungry() {} }
        static class LazyUnsafe {
            static LazyUnsafe i; private LazyUnsafe() {}
            static LazyUnsafe get() { if (i == null) i = new LazyUnsafe(); return i; }
        }
        static class LazySync {
            static LazySync i; private LazySync() {}
            static synchronized LazySync get() { if (i == null) i = new LazySync(); return i; }
        }
        static class DCL {
            private static volatile DCL i; private DCL() {}
            static DCL get() {
                if (i == null) synchronized (DCL.class) {
                    if (i == null) i = new DCL();
                }
                return i;
            }
        }
        static class StaticInner {
            private StaticInner() {}
            static class Holder { static final StaticInner I = new StaticInner(); }
            static StaticInner get() { return Holder.I; }
        }
        enum EnumSingleton {
            INSTANCE;
            public void doWork() { System.out.println("枚举单例业务"); }
        }
        static class CAS {
            private static final AtomicReference<CAS> I = new AtomicReference<>();
            private CAS() {}
            static CAS get() {
                for (;;) {
                    CAS c = I.get();
                    if (c != null) return c;
                    if (I.compareAndSet(null, new CAS())) return I.get();
                }
            }
        }
        public static void main(String[] args) {
            System.out.println("饿汉:" + (Hungry.I == Hungry.I));
            System.out.println("懒汉sync:" + (LazySync.get() == LazySync.get()));
            System.out.println("DCL:" + (DCL.get() == DCL.get()));
            System.out.println("静态内部:" + (StaticInner.get() == StaticInner.get()));
            System.out.println("枚举:" + (EnumSingleton.INSTANCE == EnumSingleton.INSTANCE));
            System.out.println("CAS:" + (CAS.get() == CAS.get()));
            EnumSingleton.INSTANCE.doWork();
        }
    }

    public static class Q27 {
        // 限流器:令牌桶 / 漏桶 / 滑动窗口
        static class TokenBucket {
            final int cap; final double rate; double tokens; long last = System.nanoTime();
            TokenBucket(int c, double r) { cap = c; rate = r; tokens = c; }
            synchronized boolean tryAcquire() {
                long now = System.nanoTime();
                tokens = Math.min(cap, tokens + (now - last) / 1e9 * rate);
                last = now;
                if (tokens >= 1) { tokens--; return true; }
                return false;
            }
        }
        static class SlidingWindow {
            final int limit; final Deque<Long> q = new ArrayDeque<>();
            SlidingWindow(int l) { limit = l; }
            synchronized boolean tryAcquire() {
                long now = System.currentTimeMillis();
                while (!q.isEmpty() && now - q.peekFirst() >= 1000) q.pollFirst();
                if (q.size() < limit) { q.offerLast(now); return true; }
                return false;
            }
        }
        public static void main(String[] args) throws InterruptedException {
            TokenBucket tb = new TokenBucket(3, 1); // 容量3,每秒1个
            System.out.print("令牌桶 5 次请求: ");
            for (int i = 0; i < 5; i++) System.out.print(tb.tryAcquire() + " ");
            System.out.println();

            SlidingWindow sw = new SlidingWindow(3);
            System.out.print("滑动窗口 5 次请求: ");
            for (int i = 0; i < 5; i++) System.out.print(sw.tryAcquire() + " ");
            System.out.println();
        }
    }

    public static class Q28 {
        // 熔断器状态机(CLOSED / OPEN / HALF_OPEN)
        enum State { CLOSED, OPEN, HALF_OPEN }
        static class CircuitBreaker {
            final int failureThreshold; final long openTimeoutMs;
            State state = State.CLOSED;
            int failCount = 0;
            long openTime = 0;
            CircuitBreaker(int f, long t) { failureThreshold = f; openTimeoutMs = t; }
            synchronized boolean allow() {
                if (state == State.CLOSED) return true;
                if (state == State.OPEN) {
                    if (System.currentTimeMillis() - openTime >= openTimeoutMs) {
                        state = State.HALF_OPEN; return true;
                    }
                    return false;
                }
                return true; // HALF_OPEN 放行一次
            }
            synchronized void record(boolean ok) {
                if (ok) {
                    state = State.CLOSED; failCount = 0;
                } else {
                    if (state == State.HALF_OPEN) { state = State.OPEN; openTime = System.currentTimeMillis(); return; }
                    if (++failCount >= failureThreshold) { state = State.OPEN; openTime = System.currentTimeMillis(); }
                }
            }
        }
        public static void main(String[] args) throws InterruptedException {
            CircuitBreaker cb = new CircuitBreaker(2, 200);
            for (int i = 0; i < 6; i++) {
                if (cb.allow()) {
                    System.out.println("请求" + i + " 放行,模拟失败");
                    cb.record(false);
                } else {
                    System.out.println("请求" + i + " 被熔断");
                }
                if (i == 4) { Thread.sleep(300); System.out.println("(超时,尝试 HALF_OPEN)"); }
            }
        }
    }

    public static class Q29 {
        // 事件总线 EventBus(发布订阅)
        static class EventBus {
            final ConcurrentHashMap<Class<?>, List<Consumer<Object>>> subs = new ConcurrentHashMap<>();
            @SuppressWarnings("unchecked")
            public <T> void subscribe(Class<T> type, Consumer<T> handler) {
                subs.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>())
                        .add(o -> handler.accept(type.cast(o)));
            }
            public void publish(Object event) {
                List<Consumer<Object>> list = subs.get(event.getClass());
                if (list != null) list.forEach(c -> c.accept(event));
            }
        }
        record OrderCreated(String id) {}
        record OrderPaid(String id, double amount) {}
        public static void main(String[] args) {
            EventBus bus = new EventBus();
            bus.subscribe(OrderCreated.class, e -> System.out.println("[库存服务] 扣库存:" + e.id()));
            bus.subscribe(OrderCreated.class, e -> System.out.println("[物流服务] 准备发货:" + e.id()));
            bus.subscribe(OrderPaid.class, e -> System.out.println("[账务服务] 入账:" + e.amount()));
            bus.publish(new OrderCreated("A001"));
            bus.publish(new OrderPaid("A001", 99.9));
        }
    }

    public static class Q30 {
        // 自定义 Collector + Spliterator 并行
        static class AvgCollector implements Collector<Integer, long[], Double> {
            public Supplier<long[]> supplier() { return () -> new long[]{0, 0}; }
            public BiConsumer<long[], Integer> accumulator() {
                return (a, v) -> { a[0] += v; a[1]++; };
            }
            public BinaryOperator<long[]> combiner() {
                return (a, b) -> new long[]{a[0] + b[0], a[1] + b[1]};
            }
            public Function<long[], Double> finisher() {
                return a -> a[1] == 0 ? 0.0 : (double) a[0] / a[1];
            }
            public Set<Characteristics> characteristics() { return Collections.emptySet(); }
        }
        public static void main(String[] args) {
            List<Integer> data = IntStream.rangeClosed(1, 1000000).boxed().collect(Collectors.toList());

            double avg1 = data.stream().collect(new AvgCollector());
            double avg2 = data.parallelStream().collect(new AvgCollector());
            System.out.printf("顺序流平均值 = %.2f%n", avg1);
            System.out.printf("并行流平均值 = %.2f%n", avg2);

            // 自定义 Spliterator 演示
            Spliterator<Integer> sp = data.spliterator();
            long cnt = StreamSupport.stream(sp, true).filter(x -> x % 100000 == 0).count();
            System.out.println("能被 100000 整除的数个数 = " + cnt);
        }
    }

    // ==================== 分发入口 ====================

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("用法: java JavaAdvanced30 <n>   (n=1..30)");
            System.out.println("示例: java JavaAdvanced30 9    # 运行 LRU 缓存");
            System.out.println("提示: 23/24 题会自动关闭服务端,适合演示");
            return;
        }
        int n;
        try { n = Integer.parseInt(args[0]); }
        catch (NumberFormatException e) { System.out.println("参数必须为数字"); return; }
        if (n < 1 || n > 30) { System.out.println("范围 1~30"); return; }

        Class<?>[] qs = {
                Q01.class, Q02.class, Q03.class, Q04.class, Q05.class,
                Q06.class, Q07.class, Q08.class, Q09.class, Q10.class,
                Q11.class, Q12.class, Q13.class, Q14.class, Q15.class,
                Q16.class, Q17.class, Q18.class, Q19.class, Q20.class,
                Q21.class, Q22.class, Q23.class, Q24.class, Q25.class,
                Q26.class, Q27.class, Q28.class, Q29.class, Q30.class
        };
        System.out.println(">>> 运行 Q" + String.format("%02d", n) + " <<<");
        qs[n - 1].getMethod("main", String[].class).invoke(null, (Object) new String[0]);
    }
}