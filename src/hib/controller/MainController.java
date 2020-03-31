package hib.controller;

import com.github.sarxos.webcam.Webcam;
import hib.model.Book;
import hib.model.LocaleString;
import hib.service.BookService;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {
    private final FileChooser fileChooser;
    private final FileChooser hibFileChooser;
    private final BookService bookService;

    private File avatar;
    private List<File> additional;
    private Map<String, String> nameLocale;
    private Map<String, String> authorLocale;
    private Map<String, String> descLocale;
    private Map<String, String> editionLocale;

    @FXML
    private TextField nameRu;
    @FXML
    private TextField authorRu;
    @FXML
    private TextArea descRu;
    @FXML
    private TextField editionRu;

    @FXML
    private TextField nameEn;
    @FXML
    private TextField authorEn;
    @FXML
    private TextArea descEn;
    @FXML
    private TextField editionEn;

    @FXML
    private TextField nameFr;
    @FXML
    private TextField authorFr;
    @FXML
    private TextArea descFr;
    @FXML
    private TextField editionFr;

    @FXML
    private TextField nameIt;
    @FXML
    private TextField authorIt;
    @FXML
    private TextArea descIt;
    @FXML
    private TextField editionIt;

    @FXML
    private TextField nameDe;
    @FXML
    private TextField authorDe;
    @FXML
    private TextArea descDe;
    @FXML
    private TextField editionDe;

    @FXML
    private TextField nameCs;
    @FXML
    private TextField authorCs;
    @FXML
    private TextArea descCs;
    @FXML
    private TextField editionCs;

    @FXML
    private TextField nameGr;
    @FXML
    private TextField authorGr;
    @FXML
    private TextArea descGr;
    @FXML
    private TextField editionGr;

    @FXML
    private TextField year;
    @FXML
    private TextField price;
    @FXML
    TextField pages;

    @FXML
    private ListView<ImageView> additionalListView;
    @FXML
    private ImageView avatarImage;


    public MainController() {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JPEG Files", "*.jpg")
        );

        hibFileChooser = new FileChooser();
        hibFileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(".hib Files", "*.hib")
        );

        nameLocale = new HashMap<>();
        authorLocale = new HashMap<>();
        descLocale = new HashMap<>();
        editionLocale = new HashMap<>();
        bookService = new BookService();
    }

    @FXML
    private void addAvatarAsWebCam(ActionEvent ae) {
        Webcam webcam = Webcam.getDefault();
        webcam.open();
        BufferedImage bufferedImage = webcam.getImage();

        try {
            ImageIO.write(bufferedImage, "JPG", new File("text.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        webcam.close();
    }

    @FXML
    private void chooseAvatarAsDisk(ActionEvent ae) {
        Node source = (Node) ae.getSource();
        avatar = fileChooser.showOpenDialog(source.getScene().getWindow());
        if (avatar == null) return;
        try {
            avatarImage.setImage(SwingFXUtils.toFXImage(ImageIO.read(avatar), null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addAdditionalAsDisk(ActionEvent ae) {
        Node source = (Node) ae.getSource();
        additional = new ArrayList<>();
        additional.addAll(fileChooser.showOpenMultipleDialog(
                source.getScene()
                        .getWindow()));
        List<ImageView> images = new ArrayList<>();
        additional.forEach(e -> {
            try {
                ImageView imageView = new ImageView(SwingFXUtils.toFXImage(ImageIO.read(e), null));
                imageView.setFitHeight(200);
                imageView.setFitWidth(170);
                images.add(imageView);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        additionalListView.getItems().addAll(images);

    }

    @FXML
    private void deserialize(ActionEvent ae) {
        Node source = (Node) ae.getSource();
        File hib = hibFileChooser.showOpenDialog(source.getScene().getWindow());
        if (hib == null) return;
        avatarImage.setImage(null);
        Book book = null;
        try {
            book = bookService.getBook(hib);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (book == null) return;

        setLocaleText(book.getName(), nameRu, nameEn, nameFr, nameIt, nameDe, nameCs, nameGr);
        setLocaleText(book.getAuthor(), authorRu, authorEn, authorFr, authorIt, authorDe, authorCs, authorGr);
        setLocaleText(book.getDesc(), descRu, descEn, descFr, descIt, descDe, descCs, descGr);

        if (book.getAvatar() != null) {
            try {
                avatarImage.setImage(SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(book.getAvatar())), null));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void serialize(ActionEvent ae) {
        Node source = (Node) ae.getSource();
        File target = hibFileChooser.showSaveDialog(source.getScene().getWindow());

        if (target == null) return;

        getLocaleText(nameLocale, nameRu, nameEn, nameFr, nameIt, nameDe, nameCs, nameGr);
        getLocaleText(authorLocale, authorRu, authorEn, authorFr, authorIt, authorDe, authorCs, authorGr);
        getLocaleText(descLocale, descRu, descEn, descFr, descIt, descDe, descCs, descGr);
        getLocaleText(editionLocale, editionRu, editionEn, editionFr, editionIt, editionDe, editionCs, editionGr);

        Book book = new Book(new LocaleString(nameLocale),
                new LocaleString(authorLocale),
                new LocaleString(descLocale),
                new LocaleString(editionLocale));

        book.setYearOfEdition(year.getText());
        if (!pages.getText().equals("")) book.setPages(Long.parseLong(pages.getText()));
        if (!price.getText().equals("")) book.setPrice(Long.parseLong(price.getText()));

        try {
            List<byte[]> additionalBytes = new ArrayList<>();
            if (additional != null) {
                for (File file : additional) {
                    additionalBytes.add(convertImage(file));
                }
            }
            book.setAdditionalPhotos(additionalBytes);
            if (avatar != null) book.setAvatar(convertImage(avatar));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            bookService.saveBook(book, target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cancel(ActionEvent ae) {
        clearText(nameRu, nameEn, nameFr, nameIt, nameDe, nameCs, nameGr);
        clearText(authorRu, authorEn, authorFr, authorIt, authorDe, authorCs, authorGr);
        clearText(descRu, descEn, descFr, descIt, descDe, descCs, descGr);
        clearText(editionRu, editionEn, editionFr, editionIt, editionDe, editionCs, editionGr);
        price.setText("");
        year.setText("");
        pages.setText("");
        avatarImage.setImage(null);
        avatar = null;
        additional = null;
        additionalListView.getItems().clear();
    }

    private void clearText(TextInputControl ru,
                           TextInputControl en, TextInputControl fr,
                           TextInputControl it, TextInputControl de,
                           TextInputControl cs, TextInputControl gr) {
        ru.setText("");
        en.setText("");
        fr.setText("");
        it.setText("");
        de.setText("");
        cs.setText("");
        gr.setText("");
    }


    private byte[] convertImage(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    private void setLocaleText(LocaleString localeString, TextInputControl ru,
                               TextInputControl en, TextInputControl fr,
                               TextInputControl it, TextInputControl de,
                               TextInputControl cs, TextInputControl gr) {
        ru.setText(localeString.getRu());
        en.setText(localeString.getEn());
        fr.setText(localeString.getFr());
        it.setText(localeString.getIt());
        de.setText(localeString.getDe());
        cs.setText(localeString.getCs());
        gr.setText(localeString.getGr());
    }

    private void getLocaleText(Map<String, String> localeMap, TextInputControl ru,
                               TextInputControl en, TextInputControl fr,
                               TextInputControl it, TextInputControl de,
                               TextInputControl cs, TextInputControl gr) {
        localeMap.put("ru", ru.getText());
        localeMap.put("en", en.getText());
        localeMap.put("fr", fr.getText());
        localeMap.put("it", it.getText());
        localeMap.put("de", de.getText());
        localeMap.put("cs", cs.getText());
        localeMap.put("gr", gr.getText());
    }
}
