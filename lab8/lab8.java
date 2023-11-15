/* Disciplina: Programacao Concorrente */
/* Prof.: Silvana Rossetto */
/* Laboratório: 8 */
/* Codigo: Criando um pool de threads em Java */

import java.util.LinkedList;
import java.lang.Math; 

//-------------------------------------------------------------------------------
//Classe que define um pool de threads 
//!!! Documentar essa classe !!!
class FilaTarefas {
    private final int nThreads;
    private final MyPoolThreads[] threads;
    private final LinkedList<Runnable> queue;
    private boolean shutdown;

    public FilaTarefas(int nThreads) {
        this.shutdown = false;
        this.nThreads = nThreads;
        queue = new LinkedList<Runnable>();
        threads = new MyPoolThreads[nThreads];
        for (int i=0; i<nThreads; i++) {
            threads[i] = new MyPoolThreads();
            threads[i].start();
        } 
    }

    public void execute(Runnable r) {
        synchronized(queue) {
            if (this.shutdown) return;
            queue.addLast(r);
            queue.notify();
        }
    }
    
    public void shutdown() {
        synchronized(queue) {
            this.shutdown=true;
            queue.notifyAll();
        }
        for (int i=0; i<nThreads; i++) {
          try { threads[i].join(); } catch (InterruptedException e) { return; }
        }
    }

    private class MyPoolThreads extends Thread {
       public void run() {
         Runnable r;
         while (true) {
           synchronized(queue) {
             while (queue.isEmpty() && (!shutdown)) {
               try { queue.wait(); }
               catch (InterruptedException ignored){}
             }
             if (queue.isEmpty() && shutdown) return;  
             r = (Runnable) queue.removeFirst();
           }
           try { r.run(); }
           catch (RuntimeException e) {}
         } 
       } 
    } 
}
//-------------------------------------------------------------------------------

class EhPrimo implements Runnable{

    private int num  = 0;
    private static int qtdPrimos = 0;

    //classe construturo
    public EhPrimo(int n){
        this.num = n;
    }

    //verifica se num é primo
    public void run(){
        if (this.num <= 1){
            return;
        }
        if (this.num == 2){
            incrementaQtdPrimos();
            return;
        }
        if (this.num % 2 == 0){
            return;
        }
        for (int i = 3; i < Math.sqrt(this.num) + 1; i += 2){
            if (this.num % i == 0){
                return;
            }
        }
        incrementaQtdPrimos();
        return;
    }

    //Incrementa a quantidade de números primos com exclusão mútua
    public static synchronized void incrementaQtdPrimos(){
        qtdPrimos++;
    }

    // Printa a quantidade de números primos
    public static void printQtdPrimos(){
        System.out.println(qtdPrimos);
    }
}

//Classe da aplicação (método main)
class MyPool {
    private static final int NTHREADS = 10;

    public static void main (String[] args) {
      //--PASSO 2: cria o pool de threads
      FilaTarefas pool = new FilaTarefas(NTHREADS); 
      
      //--PASSO 3: dispara a execução dos objetos runnable usando o pool de threads
      for (int i = 0; i < 1000000; i++) {
        Runnable r = new EhPrimo(i);
        pool.execute(r);
      }

      //--PASSO 4: esperar pelo termino das threads
      pool.shutdown();

      // Printando o número de threads
      EhPrimo.printQtdPrimos();

      System.out.println("Terminou");
   }
}
