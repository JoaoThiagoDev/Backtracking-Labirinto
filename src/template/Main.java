package template;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.Color;
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
    private boolean resolvido;

    // Para armazenar a direção da seta a ser desenhada
    private int direcaoAtual;

    public Main() {
        super(800, 800, "Backtracking", 60, true);
    }

    @Override
    public void create() {
        tempoParaMudar = 0.2;
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
                avancarPasso();
            }
        }
    }

    @Override
    public void draw() {
        clearBackground(WHITE);
        desenharTabuleiro();

        // Desenhando a seta para a última direção que o algoritmo seguiu
        if (direcaoAtual != -1) {
            int[] posicao = caminho.peek();
            desenharSeta(posicao[0], posicao[1], direcaoAtual);
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

    // Novo método para desenhar a seta
    private void desenharSeta(int linha, int coluna, int direcao) {
        // Definir as coordenadas da seta
        int xStart = coluna * tamTela / 10 + 40;
        int yStart = linha * tamTela / 10 + 40;
        int xEnd = xStart;
        int yEnd = yStart;

        // Baseado na direção, ajustar as coordenadas finais da seta
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

        // Desenha a ponta da seta
        drawLine(xEnd, yEnd, arrowX1, arrowY1, BLACK);
        drawLine(xEnd, yEnd, arrowX2, arrowY2, BLACK);
    }

    private void avancarPasso() {
        if (caminho.isEmpty()) {
            resolvido = true;
            return;
        }

        int[] posicao = caminho.peek();
        int linha = posicao[0];
        int coluna = posicao[1];

        if (linha == 9 && coluna == 9) {
            resolvido = true;
            mazeTable[linha][coluna] = CELULA_FINAL;
            return;
        }

        for (int i = 0; i < 4; i++) {
            int novaLinha = linha + MOV_LINHA[i];
            int novaColuna = coluna + MOV_COLUNA[i];

            if (ehValido(novaLinha, novaColuna)) {
                mazeTable[novaLinha][novaColuna] = CAMINHO_SOLUCAO;
                caminho.push(new int[]{novaLinha, novaColuna});
                direcaoAtual = i; // Atualiza a direção que o algoritmo está seguindo
                return;
            }
        }

        mazeTable[linha][coluna] = CAMINHO_LIVRE;
        caminho.pop();
        direcaoAtual = -1; // Se o caminho for revertido, resetar a direção
    }

    private boolean ehValido(int linha, int coluna) {
        return linha >= 0 && linha < mazeTable.length && coluna >= 0 && coluna < mazeTable[0].length && mazeTable[linha][coluna] == CAMINHO_LIVRE;
    }

    public static void main(String[] args) {
        new Main();
    }
}
