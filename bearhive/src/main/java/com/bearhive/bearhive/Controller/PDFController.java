package com.bearhive.bearhive.Controller;

import java.awt.Color;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bearhive.bearhive.Model.Bill;
import com.bearhive.bearhive.Model.UserCourse;
import com.bearhive.bearhive.Service.BillService;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class PDFController {
    @Autowired
    private BillService billService;

    @GetMapping("/bill/pdf/{billId}")
    public void exportBillPdf(@PathVariable Long billId, HttpServletResponse response) throws Exception {
        Bill bill = billService.findById(billId).orElseThrow(() -> new RuntimeException("Bill not found"));

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Hoa_don_" + bill.getBillCode() + ".pdf");

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        BaseFont baseFont = BaseFont.createFont(
            getClass().getResource("/static/fonts/SVN-Times New Roman.ttf").toString(),
            BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(baseFont, 12);
        Font boldFont = new Font(baseFont, 12, Font.BOLD);
        Font titleFont = new Font(baseFont, 20, Font.BOLD);
        Font headerFont = new Font(baseFont, 16, Font.BOLD);
        Font smallFont = new Font(baseFont, 10);
        
        // Header
        Paragraph header = new Paragraph();
        header.add(new Phrase("BearHive Practice Web\n", headerFont));
        header.add(new Phrase("Email: support@gmail.com | Hotline: 0123-456-789\n", smallFont));
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(20);
        document.add(header);

        // Bill 
        Paragraph title = new Paragraph("HÓA ĐƠN THANH TOÁN", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        document.add(new Paragraph("Mã hóa đơn: " + bill.getBillCode(), font));
        String paymentMethod = bill.getOrderId().contains("VNPAY") ? "VNPay" : "MOMO";
        document.add(new Paragraph("Hình thức thanh toán: " + paymentMethod, font));
        String transactionLabel = bill.getOrderId().contains("VNPAY") ? "Mã hóa đơn VNPay" : "Mã hóa đơn MOMO";
        document.add(new Paragraph(transactionLabel + ": " + bill.getOrderId(), font));
        document.add(new Paragraph("Ngày: " + bill.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), font));
        document.add(new Paragraph("Khách hàng: " + bill.getUser().getFullname(), font));
        document.add(new Paragraph(" ", font)); 

        // Course Table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 2, 2});

        PdfPCell cell = new PdfPCell(new Phrase("STT", boldFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Khóa học", boldFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Giá gốc", boldFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Giá giảm", boldFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(cell);

        int index = 1;
        for (UserCourse userCourse : bill.getUserCourses()) {
            if (userCourse.getStatus().equals("PURCHASED")) {
                PdfPCell sttCell = new PdfPCell(new Phrase(String.valueOf(index++), font));
                sttCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(sttCell);

                table.addCell(new PdfPCell(new Phrase(userCourse.getCourse().getTitle(), font)));

                PdfPCell originalPriceCell = new PdfPCell(new Phrase(userCourse.getCourse().getOriginalPrice() + "đ", font));
                originalPriceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(originalPriceCell);

                PdfPCell discountedPriceCell = new PdfPCell(new Phrase(userCourse.getCourse().getDiscountedPrice() + "đ", font));
                discountedPriceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(discountedPriceCell);
            }
        }
        document.add(table);

        Paragraph total = new Paragraph("Tổng tiền: " + bill.getTotalAmount() + "đ", boldFont);
        total.setAlignment(Element.ALIGN_RIGHT);
        total.setSpacingBefore(20);
        document.add(total);

        Paragraph status = new Paragraph("Trạng thái thanh toán: " + bill.getPaymentStatus(), font);
        status.setAlignment(Element.ALIGN_RIGHT);
        document.add(status);

        // Footer
        Paragraph footer = new Paragraph("Cảm ơn bạn đã sử dụng dịch vụ của BearHive!", smallFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20);
        document.add(footer);

        document.close();
    }
}
