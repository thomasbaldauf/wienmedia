package com.wienmedia;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

public class AdvertPdfServlet extends HttpServlet {
	
	private static final long serialVersionUID = 8926021135159302404L;
	
	private static final Logger log = Logger.getLogger(AdvertPdfServlet.class.getName());
	

	private static final Rectangle PRINT_FORMAT = PageSize.A4;
	private static final float PRINT_MARGIN_LEFT = 40;
	private static final float PRINT_MARGIN_RIGHT = 40;
	private static final float PRINT_MARGIN_TOP = 40;
	private static final float PRINT_MARGIN_BOTTOM = 40;

	private BaseFont pdfBaseFont;
	private Font pdfFont;
	private Font pdfFontBold;
	private Font pdfFontOblique;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		ServletOutputStream out = null; 
		try {
			initializeFonts();
			Document document = new Document(PRINT_FORMAT, PRINT_MARGIN_LEFT, PRINT_MARGIN_RIGHT, PRINT_MARGIN_TOP, PRINT_MARGIN_BOTTOM);
			PdfWriter writer = PdfWriter.getInstance(document, baos); 
			document.open();			
			document.newPage();
			doPrint(document, writer);
			document.close(); 			
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public"); 
			response.setContentType( "application/pdf" );
			response.addHeader("Content-Disposition", "inline;filename=\"" + getPrintFilename().replaceAll(" ", "-") + ".pdf\"");
			response.setContentLength(baos.size()); 
			out = response.getOutputStream(); 
			baos.writeTo(out); 
			baos.flush();
		} catch (Exception e) {			
			log.log(Level.SEVERE, "Error printing " + getPrintFilename(), e);
			response.getWriter().print(e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			try { out.close(); } catch (Exception e) {}
			try { baos.close(); } catch (Exception e) {}
		}
	}

	private void doPrint(Document document, PdfWriter writer) throws DocumentException {
		Paragraph p1 = new Paragraph("Wien Media", pdfFontBold);
		p1.setSpacingBefore(0);
		p1.setAlignment(Element.ALIGN_CENTER);
		document.add(p1);
		Paragraph p2 = new Paragraph("Testseite", pdfFont);
		p2.setAlignment(Element.ALIGN_CENTER);
		p2.setSpacingBefore(10);
		p2.setSpacingAfter(30);
		document.add(p2);
		DateFormat df = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM, SimpleDateFormat.MEDIUM, Locale.GERMAN);
		Paragraph p3 = new Paragraph("Aktuelle Zeit: " + df.format(new Date()), pdfFont);
		p3.setAlignment(Element.ALIGN_CENTER);
		p3.setSpacingBefore(50);
		p3.setSpacingAfter(30);
		document.add(p3);
	}

	private synchronized void initializeFonts() throws DocumentException, IOException {
		if (pdfBaseFont == null) {
			pdfBaseFont = BaseFont.createFont("com/wienmedia/DejaVuSans.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			pdfFont = new Font(pdfBaseFont, 11, Font.NORMAL);
			pdfFontBold = new Font(pdfBaseFont, 11, Font.BOLD);
			pdfFontOblique = new Font(pdfBaseFont, 11, Font.ITALIC);
		}
	}

	private String getPrintFilename() {
		return "advertPage";
	}
}
