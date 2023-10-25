package br.com.sanittas.app.util;

import br.com.sanittas.app.service.empresa.dto.ListaEmpresa;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;


@Getter
public class ListaObj<T> {

    private ListaEmpresa[] vetor;
    private int nroElem;

    public ListaObj(int tam) {
        this.vetor = new ListaEmpresa[tam];
        this.nroElem = 0;
    }

    public void adiciona(ListaEmpresa valor) {
        if (this.nroElem >= vetor.length){
            throw new IllegalStateException("Lista cheia");
        } else {
            this.vetor[this.nroElem] = valor;
            this.nroElem++;
        }
    }

    public int contaOcorrencias(T valor) {
        int cont = 0;
        for (int i = 0; i < this.nroElem;i++) {
            if (this.vetor[i] == valor) {
                cont++;
            }
        }
        return cont;
    }

    public boolean adicionaNoInicio(ListaEmpresa valor) {
        if (this.nroElem >= this.vetor.length) {
            return false;
        }
        for (int i = vetor.length-1;i>0;i--) {
            vetor[i] = vetor[i-1];
        }
        vetor[0] = valor;
        nroElem++;
        return true;
    }

    public void exibe() {
        var string = new StringBuilder();
        for (int i = 0; i < this.nroElem; i++) {
            if (this.vetor[i] != null){
                if (i == this.nroElem - 1) {
                    string.append(this.vetor[i]);
                    break;
                }
                string.append(this.vetor[i]);
                string.append(", ");
            }
        }
        System.out.println("\n" + string);
    }

    public int busca(T valor) {
        for (int i = 0; i < this.nroElem; i++) {
            if (this.vetor[i].equals(valor)) {
                return i;
            }
        }
        return -1;
    }

    public boolean removePeloIndice(int indice) {
        if (indice < 0 || indice >= this.nroElem) {
            return false;
        } else {
            var novoVetor = new ListaEmpresa[vetor.length-1];
            for (int i = 0; i < this.nroElem; i++) {
                if (i < indice) {
                    novoVetor[i] = this.vetor[i];
                } else if (i > indice) {
                    novoVetor[i - 1] = this.vetor[i];
                }
            }
            this.vetor = novoVetor;
            this.nroElem--;
        }
        return true;
    }

    public boolean removeElemento(T valor) {
        int indice = this.busca(valor);
        if (indice == -1) {
            return false;
        } else {
            return this.removePeloIndice(indice);
        }
    }

    public int pesquisaBinaria(String valor) {
        int esquerda = 0;
        int direita = this.nroElem - 1;

        while (esquerda <= direita) {
            int meio = (esquerda + direita) / 2;

            String razaoSocialNoMeio = vetor[meio].razaoSocial().toLowerCase().replaceAll("\\s", "");
            String valorFormatado = valor.toLowerCase().replaceAll("\\s", "");

            int comparacao = valorFormatado.compareToIgnoreCase(razaoSocialNoMeio);

            if (comparacao == 0) {
                return meio; // Encontrou a empresa
            }

            if (comparacao < 0) {
                direita = meio - 1;
            } else {
                esquerda = meio + 1;
            }
        }
        return -1; // Empresa não encontrada
    }


    public void setElemento(int indice, ListaEmpresa elemento) {
        if (indice < 0 || indice >= nroElem) {   // se indice invalido
            System.out.println("\nIndice invalido!");
        }
        else {
            vetor[indice] = elemento;
        }
    }


    public ListaEmpresa getElemento(int indice) {
        if (indice > nroElem || indice < 0) {
            return null;
        }
        return this.vetor[indice];
    }
    public void limpa() {
        var novoVetor = new ListaEmpresa[vetor.length];
        this.vetor = novoVetor;
        nroElem = 0;
    }
    @JsonIgnore
    public int getTamanho() {return nroElem;}

}
