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

public class ExportPDF {
    private static Context context;
    private static File file;

    private static Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    private static Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.NORMAL);
    private static Font boldNormalFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);

    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    public ExportPDF(Context context)
    {
        this.context = context;
        file = new File("/storage/emulated/0/Android/data/CalorieTrek");
        if(!file.exists())
        {
            file.mkdir();
        }
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
        document.addTitle("CalorieTrek Trainings");
        document.addSubject("CalorieTrek Trainings");
        document.addKeywords("CalorieTrek, PDF, Training");
        document.addAuthor("CalorieTrek");
        document.addCreator("CalorieTrek Vogel");
    }

    private static void addTitlePage(Document document) throws DocumentException {
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.add(new Paragraph("CalorieTrek Trainings", titleFont));

        addEmptyLine(preface, 1);
        Paragraph logo = returnImageParagraph(R.drawable.cklogo, 175, 50);
        preface.add(logo);

        addEmptyLine(preface, 1);
        preface.add(new Paragraph("PDF created by user:", boldNormalFont));
        preface.add(new Paragraph(CurrentUser.personEmail, normalFont));

        addEmptyLine(preface, 1);
        preface.add(new Paragraph("Date:", boldNormalFont));
        preface.add(new Paragraph(getDate(), normalFont));

        addEmptyLine(preface, 1);
        preface.add(new Paragraph("This document contains training details calculated with the CalorieTrek application.", normalFont));

        addEmptyLine(preface, 1);
        preface.add(new Paragraph("Trainings can be viewed on the following pages.", normalFont));

        document.add(preface);
        document.newPage();
    }

    private static void addContent(Document document, ArrayList<TrainingModel> allTrainings) throws DocumentException  {
        //temporary trainings for testing
        allTrainings.add(new TrainingModel("1.1.2011.", "prvo", null));
        allTrainings.add(new TrainingModel("1.2.2012.", "drugo", null));
        allTrainings.add(new TrainingModel("1.3.2013.", "trece", null));
        allTrainings.add(new TrainingModel("1.4.2014.", "cetvrto", null));
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

        trainingItem.add(new Paragraph("Training Name: ", boldNormalFont));
        trainingItem.add(new Paragraph(training.getName(), normalFont));

        trainingItem.add(new Paragraph("Training Date: ", boldNormalFont));
        trainingItem.add(new Paragraph(training.getDate(), normalFont));

        trainingItem.add(new Paragraph("Training Statistics: ", boldNormalFont));
        if(!secondTrainingOnPage)
        {
            Paragraph graph = returnImageParagraph(R.drawable.cklogo, 175, 150);
            trainingItem.add(graph);
        }
        else
        {
            Paragraph graph = returnImageParagraph(R.drawable.cklogo, 175, 150);
            trainingItem.add(graph);
        }

        addTrainingTable(trainingItem, training);
        document.add(trainingItem);
    }

    private static void addTrainingTable(Paragraph paragraph, TrainingModel training) {
        PdfPTable table = new PdfPTable(2);

        //headers
        addCellToTable(table, "Stat", BaseColor.CYAN);
        addCellToTable(table, "Value", BaseColor.CYAN);
        table.setHeaderRows(1);

        //data
        addCellToTable(table, "Kcal", null);
        addCellToTable(table, "vrijednost kalorija", null);
        addCellToTable(table, "Time", null);
        addCellToTable(table, "vrijednost vremena", null);
        addCellToTable(table, "Distance", null);
        addCellToTable(table, "vrijednost udaljenosti", null);
        addCellToTable(table, "Elevation Gain", null);
        addCellToTable(table, "vrijednost visine", null);

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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String sDate = sdf.format(date.getTime());
        return sDate;
    }

    private static Paragraph returnImageParagraph(int resourceId, int scaleAbsX, int scaleAbsY) {
        try {
            Paragraph paragraph = new Paragraph();
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
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

}
