package foi.hr.calorietrek.pdf_export;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.style.BackgroundColorSpan;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.bumptech.glide.request.target.ImageViewTargetFactory;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.PdfWriter;

import foi.hr.calorietrek.R;
import foi.hr.calorietrek.model.CurrentUser;
import foi.hr.calorietrek.model.TrainingModel;
import foi.hr.calorietrek.ui.login.view.LoginActivity;

public class ExportPDF {
    private static Context context;
    private static File file;

    private static Font titleFont;
    private static Font normalFont;
    private static Font boldNormalFont;


    public ExportPDF(Context context)
    {
        this.context = context;
        file = new File(context.getString(R.string.pdf_save_path));
        if(!file.exists())
        {
            file.mkdir();
        }
        initializeFonts();
    }

    public void writePDF(ArrayList<TrainingModel> allTrainings)
    {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file + "/" + returnNewPdfName()));
            document.open();
            addMetaData(document);
            addTitlePage(document);
            addContent(document, new ArrayList<TrainingModel>());
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMetaData(Document document) {
        document.addTitle(context.getString(R.string.pdf_meta_title));
        document.addSubject(context.getString(R.string.pdf_meta_subject));
        document.addKeywords(context.getString(R.string.pdf_meta_keywords));
        document.addAuthor(context.getString(R.string.pdf_meta_author));
        document.addCreator(context.getString(R.string.pdf_meta_creator));
    }

    private static void addTitlePage(Document document) throws DocumentException {
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, context.getResources().getInteger(R.integer.pdf_empty_line_number_after_start));
        preface.add(new Paragraph(context.getString(R.string.pdf_title), titleFont));

        addEmptyLine(preface, context.getResources().getInteger(R.integer.pdf_empty_line_number_after_title));
        Paragraph logo = returnImageParagraph(R.drawable.cklogo, context.getResources().getInteger(R.integer.pdf_logo_length),
                                                                 context.getResources().getInteger(R.integer.pdf_logo_height));
        preface.add(logo);

        addEmptyLine(preface, context.getResources().getInteger(R.integer.pdf_empty_line_number_after_logo));
        preface.add(new Paragraph(context.getString(R.string.pdf_createdBy), boldNormalFont));
        preface.add(new Paragraph(CurrentUser.personEmail, normalFont));

        addEmptyLine(preface, context.getResources().getInteger(R.integer.pdf_empty_line_number_after_createdBy));
        preface.add(new Paragraph(context.getString(R.string.pdf_date), boldNormalFont));
        preface.add(new Paragraph(formatDate(getDate()), normalFont));

        addEmptyLine(preface, context.getResources().getInteger(R.integer.pdf_empty_line_number_after_date));
        preface.add(new Paragraph(context.getString(R.string.pdf_description), normalFont));

        addEmptyLine(preface, context.getResources().getInteger(R.integer.pdf_empty_line_number_after_description));
        preface.add(new Paragraph(context.getString(R.string.pdf_instructions), normalFont));

        document.add(preface);
        document.newPage();
    }

    private static void addContent(Document document, ArrayList<TrainingModel> allTrainings) throws DocumentException  {
        //temporary trainings for testing
        allTrainings.add(new TrainingModel(1,"1.1.2011.", "prvo", null, 0));
        allTrainings.add(new TrainingModel(2,"1.2.2012.", "drugo", null, 0));
        allTrainings.add(new TrainingModel(3,"1.3.2013.", "trece", null, 0));
        allTrainings.add(new TrainingModel(4,"1.4.2014.", "cetvrto", null, 0));
        //temporary trainings for testing

        boolean goToNewPage = false;
        for(TrainingModel training : allTrainings)
        {
            addTraining(document, training, goToNewPage);
            if(goToNewPage)
            {
                document.newPage();
                goToNewPage = false;
            }
            else
            {
                goToNewPage = true;
            }
        }

    }

    private static void addTraining(Document document, TrainingModel training, boolean secondTrainingOnPage) throws DocumentException  {
        Paragraph trainingItem = new Paragraph();

        trainingItem.add(new Paragraph(context.getString(R.string.pdf_training_name), boldNormalFont));
        trainingItem.add(new Paragraph(training.getName(), normalFont));

        trainingItem.add(new Paragraph(context.getString(R.string.pdf_training_date), boldNormalFont));
        trainingItem.add(new Paragraph(training.getDate(), normalFont));

        trainingItem.add(new Paragraph(context.getString(R.string.pdf_training_stats), boldNormalFont));
        if(!secondTrainingOnPage)
        {
            Paragraph graph = returnImageParagraph(R.drawable.cklogo, context.getResources().getInteger(R.integer.pdf_graph_length),
                                                                      context.getResources().getInteger(R.integer.pdf_graph_length));
            trainingItem.add(graph);
        }
        else
        {
            Paragraph graph = returnImageParagraph(R.drawable.cklogo, context.getResources().getInteger(R.integer.pdf_graph_length),
                                                                      context.getResources().getInteger(R.integer.pdf_graph_length));
            trainingItem.add(graph);
        }

        addTrainingTable(trainingItem, training);
        document.add(trainingItem);
    }

    private static void addTrainingTable(Paragraph paragraph, TrainingModel training) {
        PdfPTable table = new PdfPTable(context.getResources().getInteger(R.integer.pdf_table_column_count));

        //headers
        addCellToTable(table, context.getString(R.string.pdf_table_stat), BaseColor.CYAN);
        addCellToTable(table, context.getString(R.string.pdf_table_value), BaseColor.CYAN);
        table.setHeaderRows(context.getResources().getInteger(R.integer.pdf_table_header_row_count));

        //data
        addCellToTable(table, context.getString(R.string.pdf_table_Kcal), null);
        addCellToTable(table, "vr. kalorija (placeholder)", null);
        addCellToTable(table, context.getString(R.string.pdf_table_time), null);
        addCellToTable(table, "vr. vremena (placeholder)", null);
        addCellToTable(table, context.getString(R.string.pdf_table_distance), null);
        addCellToTable(table, "vr. udaljenosti (placeholder)", null);
        addCellToTable(table, context.getString(R.string.pdf_table_elevationGain), null);
        addCellToTable(table, "vr. visine (placeholder)", null);

        paragraph.add(table);
    }

    private static void addCellToTable(PdfPTable table, String value, BaseColor color) {
        PdfPCell c1 = new PdfPCell(new Phrase(value));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        if(color != null)
        {
            c1.setBackgroundColor(color);
        }
        table.addCell(c1);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private static String getDate()
    {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.pdf_date_format));
        String sDate = sdf.format(date.getTime());
        return sDate;
    }

    private static String formatDate(String date)
    {
        String sDate = date;
        sDate = date.replaceAll("/", ".");
        sDate += ".";
        return sDate;
    }

    private static Paragraph returnImageParagraph(int resourceId, int scaleAbsX, int scaleAbsY) {
        try {
            Paragraph paragraph = new Paragraph();
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, context.getResources().getInteger(R.integer.pdf_bitmap_quality), stream);
            byte[] byteArray = stream.toByteArray();

            Image img = Image.getInstance(byteArray);
            img.scaleAbsolute(scaleAbsX, scaleAbsY);
            paragraph.add(img);
            return paragraph;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String returnNewPdfName() {
        String name = getDate();
        name = name.replaceAll("/", "_");
        name += "_" + CurrentUser.personEmail.split("@")[0];

        //adds a different number to the end of the pdf name in case of multiple exports in the same day
        int i = 1;
        File newFile;
        boolean fileNameFree = false;
        while(!fileNameFree)
        {
            newFile = new File(file + "/" + name + "_" + String.valueOf(i) + ".pdf");
            if(newFile.exists())
            {
                i++;
            }
            else
            {
                fileNameFree = true;
            }
        }
        return name + "_" + String.valueOf(i) + ".pdf";
    }

    private static void initializeFonts() {
        titleFont = new Font(Font.FontFamily.TIMES_ROMAN, context.getResources().getInteger(R.integer.pdf_font_size_title), Font.BOLD);
        normalFont = new Font(Font.FontFamily.TIMES_ROMAN, context.getResources().getInteger(R.integer.pdf_font_size_normal), Font.NORMAL);
        boldNormalFont = new Font(Font.FontFamily.TIMES_ROMAN, context.getResources().getInteger(R.integer.pdf_font_size_normalBold), Font.BOLD);
    }
}
