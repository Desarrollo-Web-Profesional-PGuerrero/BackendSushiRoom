package com.sushiroom.backend.controllers;

import com.sushiroom.backend.models.Producto;
import com.sushiroom.backend.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class SitemapController {

    @Autowired
    private ProductoService productoService;

    @GetMapping(value = "/sitemap.xml", produces = "application/xml")
    public String getSitemap() {
        StringBuilder sitemap = new StringBuilder();

        sitemap.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sitemap.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        // Páginas estáticas del frontend
        sitemap.append(createUrl("https://tusushiroom.com/", LocalDate.now(), "daily", "1.0"));
        sitemap.append(createUrl("https://tusushiroom.com/menu", LocalDate.now(), "daily", "0.9"));
        sitemap.append(createUrl("https://tusushiroom.com/experiencia", LocalDate.now(), "weekly", "0.8"));
        sitemap.append(createUrl("https://tusushiroom.com/carrito", LocalDate.now(), "weekly", "0.7"));
        sitemap.append(createUrl("https://tusushiroom.com/login", LocalDate.now(), "monthly", "0.6"));

        // Productos dinámicos desde la base de datos
        List<Producto> productos = productoService.findAllActivos();
        for (Producto producto : productos) {
            String url = "https://tusushiroom.com/producto/" + producto.getId();
            sitemap.append(createUrl(url, LocalDate.now(), "weekly", "0.7"));
        }

        sitemap.append("</urlset>");
        return sitemap.toString();
    }

    private String createUrl(String loc, LocalDate lastmod, String changefreq, String priority) {
        return String.format(
                "<url>\n" +
                        "  <loc>%s</loc>\n" +
                        "  <lastmod>%s</lastmod>\n" +
                        "  <changefreq>%s</changefreq>\n" +
                        "  <priority>%s</priority>\n" +
                        "</url>\n",
                loc, lastmod, changefreq, priority
        );
    }
}