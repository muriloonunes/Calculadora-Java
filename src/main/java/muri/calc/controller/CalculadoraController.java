/*
 * Copyright (C) 2024. Murilo Nunes & Hartur Sales
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package muri.calc.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import muri.calc.model.historico.HistoricoModel;
import muri.calc.model.operacoes.CalculadoraModel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Murilo Nunes & Hartur Sales
 * @date 08/10/2024
 * @brief Class HelloController
 */

public class CalculadoraController {
    //TE AMO TAYLOR SWIFT
    private Scene scene;
    private final NumberFormat FORMATAR = NumberFormat.getInstance(Locale.forLanguageTag("pt-BR"));
    private static final DecimalFormat FORMATO_DECIMAL = new DecimalFormat("#,##0.#####");

    public CalculadoraModel calc = new CalculadoraModel();
    public HistoricoModel hist = new HistoricoModel();

    //botoes de operaçao
    @FXML
    private Button botaoDividir, botaoMultiplicar, botaoSubtrair, botaoSomar, botaoIgual, botaoQuadrado, botaoPorcent, botaoRaiz;

    //botoes de numeros
    @FXML
    private Button botaoUm, botaoDois, botaoTres, botaoQuatro, botaoCinco, botaoSeis, botaoSete, botaoOito, botaoNove, botaoZero;

    //botoes de gerenciamento
    @FXML
    private Button botaoMudarSinal, botaoAc, botaoDecimal, botaoApagar, botaoTema;

    @FXML
    public Label resultadoTexto, reviewTexto;

    @FXML
    private ImageView imagemTema, deleteImagem;

    @FXML
    public VBox textVbox;

    private final String temaEscuro = Objects.requireNonNull(getClass().getResource("/styles/calculadora.css")).toExternalForm();
    private final String temaClaro = Objects.requireNonNull(getClass().getResource("/styles/calculadora-branca.css")).toExternalForm();
    private boolean temaAtual = true; //true para escuro, false para claro

    public void setScene(Scene scene) {
        this.scene = scene;
        typewriterAtMyApartment();
    }

    private void typewriterAtMyApartment() {
        scene.setOnKeyPressed(event -> {
            try {
                // ignora os botões de operaçao caso um erro esteja sendo exibido
                if (calc.isExibindoErro()) {
                    switch (event.getCode()) {
                        case DIGIT0, NUMPAD0, DIGIT1, NUMPAD1, DIGIT2, NUMPAD2, DIGIT3, NUMPAD3, DIGIT4, NUMPAD4,
                             DIGIT5, NUMPAD5, DIGIT6, NUMPAD6, DIGIT7, NUMPAD7, DIGIT8, NUMPAD8, DIGIT9, NUMPAD9 -> {
                            limpar();
                            lidarNumeros(event.getText());
                        }
                        case ESCAPE -> limpar();
                        case BACK_SPACE -> botaoGerenClicado(new ActionEvent(botaoApagar, Event.NULL_SOURCE_TARGET));
                        case PERIOD, DECIMAL, COMMA -> botaoGerenClicado(new ActionEvent(botaoDecimal, Event.NULL_SOURCE_TARGET));
                    }
                } else {
                    switch (event.getCode()) {
                        case ESCAPE -> limpar();
                        case BACK_SPACE -> botaoGerenClicado(new ActionEvent(botaoApagar, Event.NULL_SOURCE_TARGET));
                        case PERIOD, DECIMAL, COMMA -> botaoGerenClicado(new ActionEvent(botaoDecimal, Event.NULL_SOURCE_TARGET));
                        case ENTER -> botaoIgualClicado();
                        case ADD -> botaoOperacaoClicado(new ActionEvent(botaoSomar, Event.NULL_SOURCE_TARGET));
                        case SUBTRACT -> botaoOperacaoClicado(new ActionEvent(botaoSubtrair, Event.NULL_SOURCE_TARGET));
                        case MULTIPLY -> botaoOperacaoClicado(new ActionEvent(botaoMultiplicar, Event.NULL_SOURCE_TARGET));
                        case DIVIDE -> botaoOperacaoClicado(new ActionEvent(botaoDividir, Event.NULL_SOURCE_TARGET));
                        case DIGIT0, NUMPAD0, DIGIT1, NUMPAD1, DIGIT2, NUMPAD2, DIGIT3, NUMPAD3, DIGIT4, NUMPAD4,
                             DIGIT5, NUMPAD5, DIGIT6, NUMPAD6, DIGIT7, NUMPAD7, DIGIT8, NUMPAD8, DIGIT9, NUMPAD9 ->
                                lidarNumeros(event.getText());
                    }
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setStage(Stage stage) {
        stage.setOnCloseRequest(this::fecharApp);
    }

    private void fecharApp(WindowEvent event) {
        hist.criarArquivo(calc.logging);
    }

    @FXML
    private void initialize() {
        resultadoTexto.setStyle("-fx-font-size: " + 70 + "px;");
        resultadoTexto.setText("");
        reviewTexto.setText("");

        // permite que resultadoTexto tenha a fonte ajustada automaticamente
        // https://stackoverflow.com/questions/54495381/how-to-dynamically-change-font-size-in-ui-to-always-be-the-same-width-in-javafx
        double defaultFontSize = 70;
        Font defaultFont = Font.font("Hind Siliguri Medium", 70);

        TextField tf = new TextField(resultadoTexto.getText());
        resultadoTexto.setStyle("-fx-font-size: " + defaultFontSize + "px;");

        resultadoTexto.textProperty().addListener((observable, oldValue, newValue) -> {
            double MAX_TEXT_WIDTH = textVbox.getWidth();

            Text tmpText = new Text(newValue);
            tmpText.setFont(defaultFont);

            double textWidth = tmpText.getLayoutBounds().getWidth();

            if (textWidth <= MAX_TEXT_WIDTH) {
                resultadoTexto.setStyle("-fx-font-size: " + defaultFontSize + "px;");
            } else {
                double newFontSize = defaultFontSize * MAX_TEXT_WIDTH / textWidth;
                resultadoTexto.setStyle("-fx-font-size: " + newFontSize + "px;");
            }

        });
        resultadoTexto.textProperty().bindBidirectional(tf.textProperty());
    }

    public void mudarTema() {
        if (temaAtual) {
            aplicarTema(temaClaro, "/images/dark-mode.png", "/images/backspace-dark.png");
        } else {
            aplicarTema(temaEscuro, "/images/light-mode.png", "/images/backspace.png");
        }
        temaAtual = !temaAtual;
    }

    private void aplicarTema(String tema, String temaImgPath, String deleteImgPath) {
        atualizarImagens(temaImgPath, deleteImgPath);
        scene.getStylesheets().remove(temaAtual ? temaEscuro : temaClaro);
        scene.getStylesheets().add(tema);
    }

    private void atualizarImagens(String temaImg, String deleteImg) {
        imagemTema.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(temaImg))));
        deleteImagem.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(deleteImg))));
    }

    public void botaoNumeroClicado(ActionEvent click) {
        if (calc.isExibindoErro()) {
            limpar();
        }
        String valor = ((Button) click.getSource()).getText();
        lidarNumeros(valor);
    }

    public void lidarNumeros(String numero) {
        //se resultado calculado for false, nao limpa. se for true, limpa
        if (calc.isResultadoCalculado()) {
            limpar();
        }
        resultadoTexto.setText(resultadoTexto.getText() + numero);
    }

    public void botaoGerenClicado(ActionEvent e) {
        if (calc.isExibindoErro()) {
            limpar();
        }

        String textoAtual = resultadoTexto.getText();

        if (e.getSource() == botaoDecimal) {
            if (textoAtual.isEmpty() || calc.isResultadoCalculado()) {
                limpar();
                resultadoTexto.setText("0,");
            } else if (!textoAtual.contains(",")) {
                resultadoTexto.setText(textoAtual + ",");
            }
        } else if (e.getSource() == botaoMudarSinal) {
            try {
                String input = resultadoTexto.getText();
                if (!input.isEmpty()) {
                    Number num = FORMATAR.parse(input);
                    double valor = num.doubleValue();
                    valor = -valor;
                    resultadoTexto.setText(FORMATO_DECIMAL.format(valor));
                }
            } catch (ParseException parseException) {
                resultadoTexto.setText("Erro de formatação");
                parseException.printStackTrace();
            }
        } else if (e.getSource() == botaoApagar && !textoAtual.isEmpty()) {
            resultadoTexto.setText(textoAtual.substring(0, textoAtual.length() - 1));
        }
    }

    public void botaoOperacaoClicado(ActionEvent actionEvent) throws ParseException {
        desabiltarBotoes(false);

        char operadorTemporario = '\0';
        if (actionEvent.getSource() == botaoSomar) {
            operadorTemporario = '+';
        } else if (actionEvent.getSource() == botaoSubtrair) {
            operadorTemporario = '-';
        } else if (actionEvent.getSource() == botaoMultiplicar) {
            operadorTemporario = 'x';
        } else if (actionEvent.getSource() == botaoDividir) {
            operadorTemporario = '/';
        } else if (actionEvent.getSource() == botaoQuadrado) {
            operadorTemporario = '^';
        } else if (actionEvent.getSource() == botaoPorcent) {
            operadorTemporario = '%';
        }

        // permite contas consecutivas e que o usuario altere o operador antes de digitar o segundo numero
        if (calc.isOperadorSelecionado() && !resultadoTexto.getText().isEmpty()) {
            double numeroAtual = FORMATAR.parse(resultadoTexto.getText()).doubleValue();
            calc.setNum2(numeroAtual);

            double resultado = calc.definirOperacao(calc.getNum1(), calc.getOperador(), calc.getNum2());
            resultadoTexto.setText(formatarResultado(resultado));
            calc.addCalculo(formatarResultado(calc.getNum1()) + " " + calc.getOperador() + " " + formatarResultado(calc.getNum2()) + " = " + formatarResultado(resultado));
            calc.setNum1(resultado);  // salva o resultado parcial para a proxima operação
        } else  if (!resultadoTexto.getText().isEmpty()) {
            // caso seja a primeira operação
            double numero = FORMATAR.parse(resultadoTexto.getText()).doubleValue();
            calc.setNum1(numero);
        }

        calc.setOperador(operadorTemporario);
        reviewTexto.setText(formatarResultado(calc.getNum1()) + " " + calc.getOperador());
        resultadoTexto.setText("");
        calc.setOperadorSelecionado(true);
        calc.setResultadoCalculado(false);
    }

    public void botaoRaizClicado() throws ParseException {
        if (!resultadoTexto.getText().isEmpty()) {
            try {
                double numero = FORMATAR.parse(resultadoTexto.getText()).doubleValue();
                calc.setNum1(numero);
                calc.setOperador('r');
                reviewTexto.setText("√" + formatarResultado(calc.getNum1()));
                calc.setResultado(calc.calcularRaiz(numero));
                reviewTexto.setText("√" + formatarResultado(calc.getNum1()));
                resultadoTexto.setText(formatarResultado(calc.getResultado()));
                calc.setNum1(calc.getResultado());
                calc.addCalculo(reviewTexto.getText() + " = " + resultadoTexto.getText());
                calc.setResultadoCalculado(true);
                botaoIgual.setDisable(true);
            } catch (ArithmeticException e) {
                exibirErro(e.getMessage());
            }
        }
    }

    public void botaoIgualClicado() {
        try {
            if (!resultadoTexto.getText().isEmpty()) {
                double num2 = FORMATAR.parse(resultadoTexto.getText()).doubleValue();
                if (calc.getOperador() != '\0') {
                    calc.setNum2(num2);
                    reviewTexto.setText(formatarResultado(calc.getNum1()) + " " + calc.getOperador() + " "
                            + formatarResultado(calc.getNum2()) + " =");
                    calc.setResultado(calc.definirOperacao(calc.getNum1(), calc.getOperador(), calc.getNum2()));
                    resultadoTexto.setText(formatarResultado(calc.getResultado()));
                    calc.setNum1(calc.getResultado());
                } else {
                    // se o usuário digitar apenas um numero e apertar igual
                    // o programa computa apenas como 5 =
                    reviewTexto.setText(formatarResultado(num2) + " =");
                    resultadoTexto.setText(formatarResultado(num2));
                }
                calc.addCalculo(reviewTexto.getText() + " " + resultadoTexto.getText());
            } else if (calc.getOperador() != '\0') {
                // repete a última operação, caso o usuario digite apenas 5 + e aperte igual
                // o programa computa a operaçao como 5 + 5
                double num2 = calc.getNum1();
                calc.setNum2(num2);
                reviewTexto.setText(formatarResultado(calc.getNum1()) + " " + calc.getOperador() + " "
                        + formatarResultado(calc.getNum2()) + " =");
                calc.setResultado(calc.definirOperacao(calc.getNum1(), calc.getOperador(), calc.getNum2()));
                resultadoTexto.setText(formatarResultado(calc.getResultado()));
                calc.setNum1(calc.getResultado());
                calc.addCalculo(reviewTexto.getText() + " " + resultadoTexto.getText());
            }
            calc.setOperadorSelecionado(false);
            calc.setResultadoCalculado(true);
        } catch (ArithmeticException | ParseException e) {
            exibirErro(e.getMessage());
        }
    }

    @FXML
    private void limpar() {
        calc.setNum1(0.0);
        calc.setNum2(0.0);
        calc.setResultado(0.0);
        calc.setOperador('\0');
        calc.setOperadorSelecionado(false);
        calc.setResultadoCalculado(false);
        resultadoTexto.setText("");
        reviewTexto.setText("");
        calc.setExibindoErro(false);
        desabiltarBotoes(false);
    }

    private void exibirErro(String mensagemErro) {
        resultadoTexto.setText(mensagemErro);
        calc.setOperadorSelecionado(false);
        calc.setResultadoCalculado(true);
        calc.setExibindoErro(true);
        desabiltarBotoes(true);
    }

    private void desabiltarBotoes(boolean b) {
        botaoIgual.setDisable(b);
        botaoSomar.setDisable(b);
        botaoSubtrair.setDisable(b);
        botaoMultiplicar.setDisable(b);
        botaoDividir.setDisable(b);
        botaoQuadrado.setDisable(b);
        botaoPorcent.setDisable(b);
        botaoRaiz.setDisable(b);
        botaoMudarSinal.setDisable(b);
    }

    private String formatarResultado(double valor) {
        return (valor % 1 != 0) ? FORMATO_DECIMAL.format(valor) : FORMATO_DECIMAL.format((int) valor);
    }
}