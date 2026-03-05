package com.oceanview.web.servlet;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

import com.oceanview.model.Bill;
import com.oceanview.model.Reservation;
import com.oceanview.service.BillingService;
import com.oceanview.dao.DAOFactory;
import com.oceanview.dao.ReservationDAO;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/admin/print-bill")
public class PrintBillServlet extends HttpServlet {

    private final BillingService billingService = new BillingService();
    private final ReservationDAO reservationDAO = DAOFactory.reservationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String reservationNo = req.getParameter("reservationNo");

        if (reservationNo == null || reservationNo.isBlank()) {
            resp.sendError(400, "Reservation number required");
            return;
        }

        try {

            Bill bill = billingService.generateBill(reservationNo);
            Reservation r = reservationDAO.findByReservationNo(reservationNo);

            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition",
                    "inline; filename=bill-" + reservationNo + ".pdf");

            OutputStream out = resp.getOutputStream();

            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, out);

            doc.open();

            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Font labelFont = new Font(Font.HELVETICA, 12, Font.BOLD);

            doc.add(new Paragraph("Ocean View Resort", titleFont));
            doc.add(new Paragraph("Reservation Bill"));
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("Reservation No: " + reservationNo, labelFont));
            doc.add(new Paragraph("Reservation ID: " + r.getReservationId()));
            doc.add(new Paragraph("Check In: " + r.getCheckIn()));
            doc.add(new Paragraph("Check Out: " + r.getCheckOut()));

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Nights: " + bill.getNights()));
            doc.add(new Paragraph("Rate: $" + bill.getRate()));
            doc.add(new Paragraph("Total: $" + bill.getTotal(), labelFont));

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Thank you for choosing Ocean View Resort"));

            doc.close();

        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }
}