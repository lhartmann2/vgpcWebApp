package com.lhp.collectionapp.controller;

import com.lhp.collectionapp.dao.GameDao;
import com.lhp.collectionapp.dao.PriceDao;
import com.lhp.collectionapp.entity.Game;
import com.lhp.collectionapp.entity.Price;
import com.lhp.collectionapp.entity.SearchResult;
import com.lhp.collectionapp.helpers.JSONFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.lhp.collectionapp.helpers.StringHelper.addDecimal;
import static com.lhp.collectionapp.helpers.StringHelper.trimString;

@Controller
public class SearchController {

    @Autowired
    PriceDao priceDao;

    @Autowired
    GameDao gameDao;
    
    private boolean searchDone = false;
    private boolean noResults = false;
    List<SearchResult> results;

    @GetMapping("/add")
    public String displayMainAddPage(Model model) {
        searchDone = false;
        noResults = false;
        model.addAttribute("searchDone", searchDone);
        model.addAttribute("noResults", noResults);
        return "add";
    }

    @GetMapping("addSearchComplete")
    public String displaySearchResults(Model model) {
        if(results.isEmpty()) {
            searchDone = false;
            noResults = true;
        }
        model.addAttribute("searchDone", searchDone);
        model.addAttribute("noResults", noResults);
        model.addAttribute("results", results);
        return "add";
    }

    @GetMapping("addGame")
    public String displayAddGame(Integer id, Model model) {
        Map<String, String> gameMap = JSONFetcher.getGameInfoById(id);
        Game game = new Game();
        game.setId(id);
        game.setProductName(gameMap.get("product-name"));
        game.setConsoleName(gameMap.get("console-name"));
        game.setReleaseDate(gameMap.get("releaseDate"));
        game.setHaveGame(false);
        game.setNew(false);
        game.setHaveBox(false);
        game.setHaveManual(false);
        game.setTotalValue(0);
        model.addAttribute("game", game);

        String newPrice = gameMap.get("newPrice");
        String cibPrice = gameMap.get("cibPrice");
        String loosePrice = gameMap.get("loosePrice");

        newPrice = addDecimal(newPrice);
        cibPrice = addDecimal(cibPrice);
        loosePrice = addDecimal(loosePrice);

        model.addAttribute("newPrice", newPrice);
        model.addAttribute("cibPrice", cibPrice);
        model.addAttribute("loosePrice", loosePrice);

        return "addGame";
    }

    @PostMapping("addGame")
    public String performAddGame(HttpServletRequest request) {
        Game game = new Game();
        int id = Integer.parseInt(request.getParameter("gameId"));
        game.setId(id);
        game.setProductName(request.getParameter("productName"));
        game.setConsoleName(request.getParameter("consoleName"));
        game.setReleaseDate(request.getParameter("releaseDate"));

        Object isNew = request.getParameter("isNew");
        Object haveGame = request.getParameter("haveGame");
        Object haveBox = request.getParameter("haveBox");
        Object haveManual = request.getParameter("haveManual");
        //Thymeleaf sucks at checkboxes
        game.setNew(isNew != null);
        game.setHaveGame(haveGame != null);
        game.setHaveBox(haveBox != null);
        game.setHaveManual(haveManual != null);

        System.out.println("New: "+game.isNew());
        System.out.println("Game: "+game.isHaveGame());
        System.out.println("Box: "+game.isHaveBox());
        System.out.println("Manual: "+game.isHaveManual());

        Price price = new Price();
        price.setGameId(game.getId());

        String newPrice = trimString(request.getParameter("newPrice"));
        String cibPrice = trimString(request.getParameter("cibPrice"));
        String loosePrice = trimString(request.getParameter("loosePrice"));

        price.setNewPrice(Float.parseFloat(newPrice));
        price.setCibPrice(Float.parseFloat(cibPrice));
        price.setLoosePrice(Float.parseFloat(loosePrice));

        if((!game.isNew()) && (!game.isHaveGame()) && (!game.isHaveBox()) && (!game.isHaveManual())) {
            //Dont add
            System.out.println("Not adding");
        } else {
            if(game.isNew()) {
                game.setHaveGame(true);
                game.setHaveBox(true);
                game.setHaveManual(true);
                game.setTotalValue(Math.round(price.getNewPrice()));
                System.out.println("Game is new");
            } else if(game.isHaveGame()) {
                if((game.isHaveBox()) && (game.isHaveManual())) {
                    game.setTotalValue(Math.round(price.getCibPrice()));
                    System.out.println("Game is CIB");
                } else {
                    game.setTotalValue(Math.round(price.getLoosePrice()));
                    System.out.println("Game is loose");
                }
            } else {
                game.setTotalValue(0); //Just manual and/or box
                System.out.println("Just manual/box");
            }
            gameDao.addGame(game);
            System.out.println("Adding to game table");
            priceDao.addPrice(price);
            System.out.println("Adding to price table");
        }
        System.out.println("redirecting");
        return "redirect:/collection";
    }
    
    @PostMapping("gameSearch")
    public String performSearch(HttpServletRequest request) {
        results = new ArrayList<>();
        results = JSONFetcher.searchGamesByName(request.getParameter("searchTerms"));
        searchDone = true;
        return "redirect:/addSearchComplete";
    }
}
