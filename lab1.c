#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>

#define NTHREADS 8
#define TAMVETOR 1000

void *elevaAoQuadrado (void *arg){
    long int * n;

    for (int i = 0; i < TAMVETOR / NTHREADS; i++){
        arg = arg + (sizeof(long int));
        n = (long int*) arg;
        *n *= *n;
    }

    pthread_exit(NULL);
}

void iniciaVetor(int *v){
    for(long int i = 0; i < TAMVETOR; i++){
        v[i] = i;
    }
}

int verificaVetor(int *v){
    for(long int i = 0; i < TAMVETOR; i++){
        if (v[i] != i * i){
            printf("Erro na posicao %d\n", i);
            return 1;
        }
    }
    return 0;
}

int main(){
    pthread_t tid_sistema[NTHREADS];

    int vetor[TAMVETOR];
    iniciaVetor(vetor);

    for(int i=0; i<NTHREADS; i++) {
        printf("--Cria a thread %d\n", i);
        if (pthread_create(&tid_sistema[i], NULL, elevaAoQuadrado, (void*) &vetor[i * (TAMVETOR / NTHREADS)])) {
        printf("--ERRO: pthread_create()\n"); exit(-1);
        }
    }

    //--espera todas as threads terminarem
    for (int i=0; i<NTHREADS; i++) {
        if (pthread_join(tid_sistema[i], NULL)) {
            printf("--ERRO: pthread_join() \n");
        } 
    }

    verificaVetor(vetor);

    pthread_exit(NULL);
}