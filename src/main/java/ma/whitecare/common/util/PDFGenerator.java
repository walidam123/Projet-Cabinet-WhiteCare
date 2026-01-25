package ma.whitecare.common.util;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class PDFGenerator {

        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        /**
         * Génère un PDF pour une ordonnance
         */
        public static byte[] generateOrdonnancePDF(
                        String patientNom,
                        String patientPrenom,
                        String medecinNom,
                        String medecinPrenom,
                        String specialite,
                        String dateOrdonnance,
                        java.util.List<PrescriptionInfo> prescriptions) throws IOException {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(baos);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf, PageSize.A4);

                // En-tête
                Paragraph header = new Paragraph("ORDONNANCE MÉDICALE")
                                .setFontSize(18)
                                .setBold()
                                .setTextAlignment(TextAlignment.CENTER)
                                .setMarginBottom(20);
                document.add(header);

                // Informations du patient
                Paragraph patientInfo = new Paragraph()
                                .add("Patient: " + patientNom + " " + patientPrenom)
                                .setMarginBottom(10);
                document.add(patientInfo);

                // Informations du médecin
                Paragraph medecinInfo = new Paragraph()
                                .add("Médecin: Dr. " + medecinNom + " " + medecinPrenom)
                                .add("\nSpécialité: " + (specialite != null ? specialite : "Non spécifiée"))
                                .setMarginBottom(10);
                document.add(medecinInfo);

                // Date
                Paragraph dateInfo = new Paragraph()
                                .add("Date: " + dateOrdonnance)
                                .setMarginBottom(20);
                document.add(dateInfo);

                // Tableau des prescriptions
                if (prescriptions != null && !prescriptions.isEmpty()) {
                        Table table = new Table(UnitValue.createPercentArray(new float[] { 3, 2, 2, 2 }))
                                        .useAllAvailableWidth()
                                        .setMarginBottom(20);

                        // En-têtes du tableau
                        table.addHeaderCell(new Cell().add(new Paragraph("Médicament").setBold())
                                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
                        table.addHeaderCell(new Cell().add(new Paragraph("Quantité").setBold())
                                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
                        table.addHeaderCell(new Cell().add(new Paragraph("Fréquence").setBold())
                                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
                        table.addHeaderCell(new Cell().add(new Paragraph("Durée (jours)").setBold())
                                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));

                        // Lignes de données
                        for (PrescriptionInfo prescription : prescriptions) {
                                table.addCell(new Cell().add(new Paragraph(prescription.getMedicamentNom())));
                                table.addCell(new Cell()
                                                .add(new Paragraph(String.valueOf(prescription.getQuantite()))));
                                table.addCell(new Cell().add(new Paragraph(
                                                prescription.getFrequence() != null ? prescription.getFrequence()
                                                                : "")));
                                table.addCell(new Cell()
                                                .add(new Paragraph(String.valueOf(prescription.getDureeEnJours()))));
                        }

                        document.add(table);
                } else {
                        Paragraph noPrescription = new Paragraph("Aucune prescription")
                                        .setItalic()
                                        .setMarginBottom(20);
                        document.add(noPrescription);
                }

                // Pied de page
                Paragraph footer = new Paragraph()
                                .add("Document généré le " + java.time.LocalDate.now().format(DATE_FORMATTER))
                                .setFontSize(8)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setMarginTop(30);
                document.add(footer);

                document.close();
                return baos.toByteArray();
        }

        /**
         * Génère un PDF pour un certificat
         */
        public static byte[] generateCertificatPDF(
                        String patientNom,
                        String patientPrenom,
                        String medecinNom,
                        String medecinPrenom,
                        String specialite,
                        String dateDebut,
                        String dateFin,
                        Integer duree,
                        String noteMedecin) throws IOException {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(baos);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf, PageSize.A4);

                // En-tête
                Paragraph header = new Paragraph("CERTIFICAT MÉDICAL")
                                .setFontSize(18)
                                .setBold()
                                .setTextAlignment(TextAlignment.CENTER)
                                .setMarginBottom(30);
                document.add(header);

                // Corps du certificat
                Paragraph body = new Paragraph()
                                .add("Je soussigné(e), Dr. " + medecinNom + " " + medecinPrenom)
                                .add((specialite != null ? ", " + specialite : ""))
                                .add(", certifie avoir examiné le patient:")
                                .setMarginBottom(15);
                document.add(body);

                // Informations du patient
                Paragraph patientInfo = new Paragraph()
                                .add("Nom: " + patientNom + " " + patientPrenom)
                                .setMarginBottom(10);
                document.add(patientInfo);

                // Période
                Paragraph periode = new Paragraph()
                                .add("Période d'incapacité: du " + dateDebut + " au " + dateFin)
                                .add("\nDurée: " + (duree != null ? duree + " jour(s)" : "Non spécifiée"))
                                .setMarginBottom(15);
                document.add(periode);

                // Note du médecin
                if (noteMedecin != null && !noteMedecin.trim().isEmpty()) {
                        Paragraph note = new Paragraph()
                                        .add("Observations médicales:")
                                        .setBold()
                                        .setMarginTop(10)
                                        .setMarginBottom(5);
                        document.add(note);

                        Paragraph noteContent = new Paragraph()
                                        .add(noteMedecin)
                                        .setMarginBottom(20);
                        document.add(noteContent);
                }

                // Signature
                Paragraph signature = new Paragraph()
                                .add("\n\n")
                                .add("Fait à WhiteCare, le " + java.time.LocalDate.now().format(DATE_FORMATTER))
                                .add("\n\n")
                                .add("Signature et cachet du médecin")
                                .setMarginTop(30);
                document.add(signature);

                // Pied de page
                Paragraph footer = new Paragraph()
                                .add("Document généré le " + java.time.LocalDate.now().format(DATE_FORMATTER))
                                .setFontSize(8)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setMarginTop(30);
                document.add(footer);

                document.close();
                return baos.toByteArray();
        }

        /**
         * Génère un PDF pour une facture
         */
        public static byte[] generateFacturePDF(
                        String patientNom,
                        String patientPrenom,
                        String patientAdresse,
                        String patientTelephone,
                        String factureId,
                        String dateFacture,
                        java.util.List<InterventionInfo> interventions,
                        Double totaleFacture,
                        Double totalePaye,
                        Double reste,
                        String statut) throws IOException {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(baos);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf, PageSize.A4);

                // En-tête avec logo et informations du cabinet
                Paragraph header = new Paragraph("FACTURE")
                                .setFontSize(20)
                                .setBold()
                                .setTextAlignment(TextAlignment.CENTER)
                                .setMarginBottom(10);
                document.add(header);

                Paragraph cabinetInfo = new Paragraph()
                                .add("WhiteCare - Cabinet Médical")
                                .setTextAlignment(TextAlignment.CENTER)
                                .setMarginBottom(30);
                document.add(cabinetInfo);

                // Informations de la facture
                Paragraph factureInfo = new Paragraph()
                                .add("N° Facture: " + factureId)
                                .add("\nDate: " + dateFacture)
                                .add("\nStatut: " + (statut != null ? statut : "N/A"))
                                .setMarginBottom(20);
                document.add(factureInfo);

                // Informations du patient
                Paragraph patientSection = new Paragraph()
                                .add("Facturé à:")
                                .setBold()
                                .setMarginBottom(5);
                document.add(patientSection);

                Paragraph patientInfo = new Paragraph()
                                .add(patientNom + " " + patientPrenom);
                if (patientAdresse != null && !patientAdresse.trim().isEmpty()) {
                        patientInfo.add("\n" + patientAdresse);
                }
                if (patientTelephone != null && !patientTelephone.trim().isEmpty()) {
                        patientInfo.add("\nTél: " + patientTelephone);
                }
                patientInfo.setMarginBottom(20);
                document.add(patientInfo);

                // Tableau des interventions (détails)
                if (interventions != null && !interventions.isEmpty()) {
                        Paragraph interventionsTitle = new Paragraph()
                                        .add("Détail des interventions:")
                                        .setBold()
                                        .setMarginBottom(10);
                        document.add(interventionsTitle);

                        Table interventionsTable = new Table(UnitValue.createPercentArray(new float[] { 3, 2, 2 }))
                                        .useAllAvailableWidth()
                                        .setMarginBottom(20);

                        interventionsTable.addHeaderCell(new Cell().add(new Paragraph("Acte").setBold())
                                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
                        interventionsTable.addHeaderCell(new Cell().add(new Paragraph("Dent").setBold())
                                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
                        interventionsTable.addHeaderCell(new Cell().add(new Paragraph("Prix (MAD)").setBold())
                                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                        .setTextAlignment(TextAlignment.RIGHT));

                        for (InterventionInfo intervention : interventions) {
                                interventionsTable.addCell(new Cell().add(new Paragraph(intervention.getActeNom())));
                                interventionsTable.addCell(new Cell().add(new Paragraph(
                                                intervention.getDent() != null ? intervention.getDent() : "Global")));
                                interventionsTable.addCell(new Cell()
                                                .add(new Paragraph(String.format("%.2f",
                                                                intervention.getPrix() != null ? intervention.getPrix()
                                                                                : 0.0)))
                                                .setTextAlignment(TextAlignment.RIGHT));
                        }

                        document.add(interventionsTable);
                }

                // Tableau récapitulatif des montants
                Paragraph summaryTitle = new Paragraph()
                                .add("Récapitulatif:")
                                .setBold()
                                .setMarginBottom(10);
                document.add(summaryTitle);

                Table table = new Table(UnitValue.createPercentArray(new float[] { 3, 2 }))
                                .useAllAvailableWidth()
                                .setMarginBottom(20);

                table.addHeaderCell(new Cell().add(new Paragraph("Description").setBold())
                                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
                table.addHeaderCell(new Cell().add(new Paragraph("Montant (MAD)").setBold())
                                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                .setTextAlignment(TextAlignment.RIGHT));

                table.addCell(new Cell().add(new Paragraph("Total facture")));
                table.addCell(new Cell()
                                .add(new Paragraph(String.format("%.2f", totaleFacture != null ? totaleFacture : 0.0)))
                                .setTextAlignment(TextAlignment.RIGHT));

                table.addCell(new Cell().add(new Paragraph("Total payé")));
                table.addCell(new Cell()
                                .add(new Paragraph(String.format("%.2f", totalePaye != null ? totalePaye : 0.0)))
                                .setTextAlignment(TextAlignment.RIGHT));

                table.addCell(new Cell().add(new Paragraph("Reste à payer").setBold()));
                table.addCell(new Cell()
                                .add(new Paragraph(String.format("%.2f", reste != null ? reste : 0.0)).setBold())
                                .setTextAlignment(TextAlignment.RIGHT)
                                .setBackgroundColor(ColorConstants.LIGHT_GRAY));

                document.add(table);

                // Notes
                Paragraph notes = new Paragraph()
                                .add("\n\n")
                                .add("Merci de votre confiance.")
                                .setMarginTop(20);
                document.add(notes);

                // Pied de page
                Paragraph footer = new Paragraph()
                                .add("Document généré le " + java.time.LocalDate.now().format(DATE_FORMATTER))
                                .setFontSize(8)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setMarginTop(30);
                document.add(footer);

                document.close();
                return baos.toByteArray();
        }

        /**
         * Classe interne pour les informations de prescription
         */
        public static class PrescriptionInfo {
                private String medicamentNom;
                private int quantite;
                private String frequence;
                private int dureeEnJours;

                public PrescriptionInfo(String medicamentNom, int quantite, String frequence, int dureeEnJours) {
                        this.medicamentNom = medicamentNom;
                        this.quantite = quantite;
                        this.frequence = frequence;
                        this.dureeEnJours = dureeEnJours;
                }

                public String getMedicamentNom() {
                        return medicamentNom;
                }

                public int getQuantite() {
                        return quantite;
                }

                public String getFrequence() {
                        return frequence;
                }

                public int getDureeEnJours() {
                        return dureeEnJours;
                }
        }

        /**
         * Classe interne pour les informations d'intervention
         */
        public static class InterventionInfo {
                private String acteNom;
                private String dent;
                private Double prix;

                public InterventionInfo(String acteNom, String dent, Double prix) {
                        this.acteNom = acteNom;
                        this.dent = dent;
                        this.prix = prix;
                }

                public String getActeNom() {
                        return acteNom;
                }

                public String getDent() {
                        return dent;
                }

                public Double getPrix() {
                        return prix;
                }
        }
}
