package hibSerializerApp.view;

import hibSerializerApp.HibSerializerApplication;
import hibSerializerApp.model.Book;
import hibSerializerApp.model.BookDTO;
import hibSerializerApp.model.LocaleString;
import hibSerializerApp.service.BookService;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

// TODO: Вынести логику в другие классы
public class MainViewController {
    private final FileChooser fileChooser;
    private final FileChooser hibFileChooser;
    private final DirectoryChooser directoryChooser;
    private final BookService bookService;
    @FXML
    TextField pages;
    private byte[] avatar;
    private List<byte[]> additional;
    private Map<String, String> nameLocale;
    private Map<String, String> authorLocale;
    private Map<String, String> descLocale;
    private Map<String, String> editionLocale;
    private List<BookDTO> previews;

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
    private ListView<ImageView> additionalListView;
    @FXML
    private ListView<HBox> previewListView;
    @FXML
    private ImageView avatarImage;
    @FXML
    private TextField searchField;

    public MainViewController() {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JPEG Files", "*.jpg")
        );

        hibFileChooser = new FileChooser();
        hibFileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(".hib Files", "*.hib")
        );

        directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select search directory");

        nameLocale = new HashMap<>();
        authorLocale = new HashMap<>();
        descLocale = new HashMap<>();
        editionLocale = new HashMap<>();
        bookService = new BookService();
    }

    @FXML
    private void searchHibFilesFromPath(ActionEvent ae) {
        if (searchField.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Select a directory before searching");
            alert.showAndWait();
            return;
        }

        List<File> previewFiles = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(searchField.getText()), 1)) {
            paths
                    .filter(e -> e.toString().endsWith(".hib"))
                    .forEach(path -> previewFiles.add(path.toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (File file : previewFiles) {
            BookDTO bookDTO = bookService.getBookDTO(file);
            Text text = new Text();
            text.setText(" " + bookDTO.getAuthor().getEn() + " - " + bookDTO.getName().getEn());
            ImageView imageView = null;
            try {
                if (bookDTO.getAvatar() != null) imageView = new ImageView(SwingFXUtils
                        .toFXImage((ImageIO.read(new ByteArrayInputStream(bookDTO.getAvatar()))), null));
                else imageView = new ImageView(SwingFXUtils
                        .toFXImage((ImageIO.read(new File("noImage.png"))), null));
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setFitWidth(100);
            imageView.setFitHeight(150);
            HBox hBox = new HBox(imageView, text);
            previewListView.getItems().addAll(hBox);
        }
    }

    @FXML
    private void selectSearchDirectory(ActionEvent ae) {
        Node source = (Node) ae.getSource();
        File dir = directoryChooser.showDialog(source.getScene().getWindow());
        directoryChooser.setInitialDirectory(dir);
        searchField.setText(dir.getAbsolutePath());
    }

    @FXML
    private void chooseAvatarFromDisk(ActionEvent ae) {
        Node source = (Node) ae.getSource();
        try {
            File choosesFile = fileChooser.showOpenDialog(source.getScene().getWindow());
            if (choosesFile == null) return;
            fileChooser.setInitialDirectory(choosesFile.getParentFile());
            avatar = convertImage(choosesFile);
            avatarImage.setImage(SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(avatar)), null));
        } catch (IOException e) {
            showError(e);
            e.printStackTrace();
        }
    }

    @FXML
    private void chooseAvatarFromWebCam(ActionEvent ae) {
        try {
            byte[] newPhoto = getPhotoFromWebCam(ae);
            if (newPhoto.length == 0) return;
            avatar = newPhoto;
            avatarImage.setImage(SwingFXUtils
                    .toFXImage(ImageIO.read(new ByteArrayInputStream(newPhoto)), null));
        } catch (IOException e) {
            showError(e);
            e.printStackTrace();
        }
    }

    @FXML
    private void addAdditionalFromWebCam(ActionEvent ae) {
        if (additional == null) additional = new ArrayList<>();
        try {
            byte[] newPhoto = getPhotoFromWebCam(ae);
            if (newPhoto.length == 0) return;
            additional.add(newPhoto);
            additionalListView.getItems().add(new ImageView(SwingFXUtils.toFXImage(ImageIO
                    .read(new ByteArrayInputStream(additional.get(additional.size() - 1))), null)));
        } catch (IOException e) {
            showError(e);
            e.printStackTrace();
        }
    }

    private byte[] getPhotoFromWebCam(ActionEvent ae) throws IOException {
        WebCamPreviewViewController controller = HibSerializerApplication.startWebCamModal();
        controller.stopCamera(ae);
        BufferedImage photo = controller.getPhoto();
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            if (photo != null) {
                ImageIO.write(photo, "jpg", byteArrayOutputStream);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

    @FXML
    private void addAdditionalFromDisk(ActionEvent ae) {
        Node source = (Node) ae.getSource();
        additional = new ArrayList<>();

        fileChooser.showOpenMultipleDialog(source.getScene().getWindow()).forEach(e -> {
            try {
                additional.add(convertImage(e));
            } catch (IOException ex) {
                showError(ex);
                ex.printStackTrace();
            }
        });

        List<ImageView> images = new ArrayList<>();
        additional.forEach(e -> {
            try {
                ImageView imageView = new ImageView(SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(e)), null));
                imageView.setFitHeight(600);
                imageView.setFitWidth(400);
                images.add(imageView);
            } catch (IOException ex) {
                showError(ex);
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
            showError(e);
            e.printStackTrace();
        }
        if (book == null) return;

        try {
            setLocaleText(book.getName(), nameRu, nameEn, nameFr, nameIt, nameDe, nameCs, nameGr);
            setLocaleText(book.getAuthor(), authorRu, authorEn, authorFr, authorIt, authorDe, authorCs, authorGr);
            setLocaleText(book.getDesc(), descRu, descEn, descFr, descIt, descDe, descCs, descGr);
            setLocaleText(book.getEdition(), editionRu, editionEn, editionFr, editionIt, editionDe, editionCs, editionGr);

            pages.setText(book.getPages().toString());
            year.setText(book.getYearOfEdition());
            price.setText(book.getPrice().toString());
        } catch (NullPointerException ignore) {
            //ignored
        }

        if (book.getAvatar() != null) {
            try {
                avatar = book.getAvatar();
                avatarImage.setImage(SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(book.getAvatar())), null));
            } catch (IOException e) {
                showError(e);
                e.printStackTrace();
            }
        }

        if (book.getAdditionalPhotos() != null) {
            List<ImageView> images = new ArrayList<>();
            additional = book.getAdditionalPhotos();
            book.getAdditionalPhotos().forEach(e -> {
                try {
                    ImageView imageView = new ImageView(SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(e)), null));
                    imageView.setFitHeight(200);
                    imageView.setFitWidth(170);
                    images.add(imageView);
                } catch (IOException ex) {
                    showError(ex);
                    ex.printStackTrace();
                }
            });
            additionalListView.getItems().addAll(images);
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
        book.setAdditionalPhotos(additional);
        if (avatar != null) book.setAvatar(avatar);
        try {
            bookService.saveBook(book, target);
        } catch (IOException e) {
            showError(e);
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

    @FXML
    private void deleteAvatar(ActionEvent ae) {
        avatar = null;
        avatarImage.setImage(null);
    }

    @FXML
    private void deleteAdditional(ActionEvent ae) {
        additional = null;
        additionalListView.getItems().clear();
    }

    private void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(e.getMessage());
        alert.setContentText(Arrays.toString(e.getStackTrace()));
        alert.showAndWait();
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