package template;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Main extends EngineFrame {

    private static final int CAMINHO_LIVRE = 1;
    private static final int PAREDE = 0;
    private static final int CAMINHO_SOLUCAO = 2;
    private static final int CELULA_FINAL = 3;

    private static final int[] MOV_LINHA = {1, 0, -1, 0};
    private static final int[] MOV_COLUNA = {0, 1, 0, -1};

    private int[][] mazeTable;
    private double contadorTempo;
    private double tempoParaMudar;
    private int tamTela;

    private Stack<int[]> caminho;

    private List<int[]> caminhoCompleto;

    private boolean resolvido;

    private int direcaoAtual;

    private int linhaDestino;
    private int colunaDestino;

    public Main() {
        super(800, 800, "Backtracking", 60, true);
    }

    @Override
    public void create() {
        caminhoCompleto = new ArrayList<>();

        linhaDestino = 3;
        colunaDestino = 3;

        tempoParaMudar = 0.05;
        tamTela = getScreenHeight();
        resolvido = false;

        mazeTable = new int[][]{
            {1, 1, 0, 1, 1, 0, 1, 1, 1, 1},
            {0, 1, 0, 0, 1, 1, 1, 0, 0, 0},
            {1, 1, 0, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 1, 1, 0, 1, 0, 1, 0, 1},
            {1, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {0, 1, 1, 1, 1, 0, 1, 1, 0, 1},
            {0, 0, 1, 0, 1, 1, 1, 1, 0, 1},
            {1, 1, 1, 0, 0, 0, 1, 1, 0, 1},
            {1, 0, 0, 0, 1, 1, 1, 0, 1, 1},
            {1, 1, 1, 1, 1, 0, 0, 1, 1, 1}
        };

        mazeTable[0][0] = CAMINHO_SOLUCAO;

        caminho = new Stack<>();
        caminho.push(new int[]{0, 0});
    }

    @Override
    public void update(double delta) {
        if (!resolvido) {
            contadorTempo += delta;
            if (contadorTempo > tempoParaMudar) {
                contadorTempo = 0;
                avancarPasso(linhaDestino, colunaDestino);
            }
        }
    }

@Override
public void draw() {
    clearBackground(WHITE);
    desenharTabuleiro();

    if (direcaoAtual != -1) {
        int[] posicao = caminho.peek();
        desenharSeta(posicao[0], posicao[1], direcaoAtual);
    }

    if (resolvido) {
        for (int[] passo : caminhoCompleto) {
            if(passo[2] > -1) desenharSeta(passo[0], passo[1], passo[2]);
        }
    }
}

    private void desenharTabuleiro() {
        Color cor;
        for (int i = 0; i < mazeTable.length; i++) {
            for (int j = 0; j < mazeTable[i].length; j++) {
                switch (mazeTable[i][j]) {
                    case PAREDE:
                        cor = BLACK;
                        break;
                    case CAMINHO_LIVRE:
                        cor = WHITE;
                        break;
                    case CAMINHO_SOLUCAO:
                        cor = SKYBLUE;
                        break;
                    case CELULA_FINAL:
                        cor = GREEN;
                        break;
                    default:
                        cor = WHITE;
                        break;
                }
                fillRectangle(j * tamTela / 10, i * tamTela / 10, 100, 100, cor);
                drawRectangle(j * tamTela / 10, i * tamTela / 10, 100, 100, BLACK);
            }
        }
    }

    private void desenharSeta(int linha, int coluna, int direcao) {
        int xStart = coluna * tamTela / 10 + 40;
        int yStart = linha * tamTela / 10 + 40;
        int xEnd = xStart;
        int yEnd = yStart;

        switch (direcao) {
            case 0: // Cima
                yEnd += 20;
                break;
            case 1: // Direita
                xEnd += 20;
                break;
            case 2: // Baixo
                yEnd -= 20;
                break;
            case 3: // Esquerda
                xEnd -= 20;
                break;
        }


        // Desenha a linha da seta
        drawLine(xStart, yStart, xEnd, yEnd, BLACK);

        // Desenha a ponta da seta
        double angle = Math.atan2(yEnd - yStart, xEnd - xStart);
        int arrowSize = 10;
        int arrowX1 = (int) (xEnd - arrowSize * Math.cos(angle - Math.PI / 6));
        int arrowY1 = (int) (yEnd - arrowSize * Math.sin(angle - Math.PI / 6));
        int arrowX2 = (int) (xEnd - arrowSize * Math.cos(angle + Math.PI / 6));
        int arrowY2 = (int) (yEnd - arrowSize * Math.sin(angle + Math.PI / 6));

        drawLine(xEnd, yEnd, arrowX1, arrowY1, BLACK);
        drawLine(xEnd, yEnd, arrowX2, arrowY2, BLACK);
    }

    private void avancarPasso(int linhaFim, int colFim) {
        if (caminho.isEmpty()) {
            resolvido = true;
            return;
        }

        int[] posicao = caminho.peek();
        int linha = posicao[0];
        int coluna = posicao[1];

        if (linha == linhaFim && coluna == colFim) {
            resolvido = true;
            mazeTable[linha][coluna] = CELULA_FINAL;
            return;
        }

        boolean encontrouCaminho = false;

        for (int i = 0; i < 4; i++) {
            int novaLinha = linha + MOV_LINHA[i];
            int novaColuna = coluna + MOV_COLUNA[i];

            if (ehValido(novaLinha, novaColuna)) {
                mazeTable[novaLinha][novaColuna] = CAMINHO_SOLUCAO;
                caminho.push(new int[]{novaLinha, novaColuna});
                caminhoCompleto.add(new int[]{linha, coluna, i}); // Caminho correto
                direcaoAtual = i;
                encontrouCaminho = true;
                break;
            }
        }

        if (!encontrouCaminho) {
            // Se não encontrou caminho, volta atrás (backtrack)
            mazeTable[linha][coluna] = -1;
            caminho.pop();
            direcaoAtual = -1;
        }
    }

    private boolean ehValido(int linha, int coluna) {
        return linha >= 0 && linha < mazeTable.length && coluna >= 0 && coluna < mazeTable[0].length && mazeTable[linha][coluna] == CAMINHO_LIVRE;
    }

    public static void main(String[] args) {
        new Main();
    }
}
