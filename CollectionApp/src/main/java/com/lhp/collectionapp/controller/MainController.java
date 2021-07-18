package com.lhp.collectionapp.controller;

import com.lhp.collectionapp.dao.GameDao;
import com.lhp.collectionapp.dao.PriceDao;
import com.lhp.collectionapp.entity.Console;
import com.lhp.collectionapp.entity.Game;
import com.lhp.collectionapp.entity.Price;
import com.lhp.collectionapp.helpers.JSONFetcher;
import com.lhp.collectionapp.helpers.StringHelper;
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

@Controller
public class MainController {

    @Autowired
    GameDao gameDao;

    @Autowired
    PriceDao priceDao;

    private List<Console> consoles;

    public MainController() {
        consoles = new ArrayList<>();
        populateConsoleList();
    }

    @GetMapping({"/", "/index"})
    public String mainPage(Model model) {
        List<Game> games = gameDao.getAllGames();
        if(games == null) {
            games = new ArrayList<>();
        } else {
            updateGamesAndPrices(games);
        }
        int totalGames = games.size();
        String totalValue = "0";
        if(!games.isEmpty()) {
            totalValue = StringHelper.addDecimal(String.valueOf(getTotalValue(games)));
        }

        model.addAttribute("totalGames", totalGames);
        model.addAttribute("totalValue", totalValue);

        return "index";
    }

    @GetMapping("update")
    public String updateData() {
        List<Game> games = gameDao.getAllGames();
        if((!games.isEmpty()) && (games != null)) {
            updateGamesAndPrices(games);
        }
        return "update";
    }

    @GetMapping("collection")
    public String displayCollection(Model model) {
        List<Game> games = gameDao.getAllGames();
        boolean hasGames = !games.isEmpty();
        model.addAttribute("games", games);
        model.addAttribute("hasGames", hasGames);
        model.addAttribute("consoles", consoles);
        return "collection";
    }

    @GetMapping("/gameDetail")
    public String displayGameDetail(Integer id, Model model) {
        Game game = gameDao.getGameById(id);
        model.addAttribute("game", game);
        return "gameDetail";
    }

    @GetMapping("editGame")
    public String displayEditGame(Integer id, Model model) {
        Game game = gameDao.getGameById(id);
        model.addAttribute("game", game);
        return "editGame";
    }

    @GetMapping("deleteGame")
    public String displayDeleteGame(Integer id, Model model) {
        Game game = gameDao.getGameById(id);
        model.addAttribute("game", game);
        return "deleteGame";
    }

    @GetMapping("performDeleteGame")
    public String performDeleteGame(Integer id, Model model) {
        gameDao.deleteGameById(id);
        return "redirect:/collection";
    }

    @PostMapping("editGame")
    public String performEditGame(HttpServletRequest request) {
        Game game = gameDao.getGameById(Integer.parseInt(request.getParameter("gameId")));
        Map<String, String> map = JSONFetcher.getGameInfoById(game.getId());

        //Update price info
        Price price = priceDao.getPriceByGameId(game.getId());
        price.setNewPrice(Float.parseFloat(addDecimal(map.get("newPrice"))));
        price.setCibPrice(Float.parseFloat(addDecimal(map.get("cibPrice"))));
        price.setLoosePrice(Float.parseFloat(addDecimal(map.get("loosePrice"))));
        priceDao.updatePrice(price);

        Object isNew = request.getParameter("isNew");
        Object haveGame = request.getParameter("haveGame");
        Object haveBox = request.getParameter("haveBox");
        Object haveManual = request.getParameter("haveManual");
        //Thymeleaf sucks at checkboxes
        game.setNew(isNew != null);
        game.setHaveGame(haveGame != null);
        game.setHaveBox(haveBox != null);
        game.setHaveManual(haveManual != null);

        if(game.isNew()) {
            game.setHaveGame(true);
            game.setHaveManual(true);
            game.setHaveBox(true);
        }

        //Update Game Value
        if(game.isNew()) { //New
            game.setTotalValue(Math.round(price.getNewPrice()));
        } else {
            if(game.isHaveGame()) {
                if ((game.isHaveManual()) && (game.isHaveBox())) { //CIB
                    game.setTotalValue(Math.round(price.getCibPrice()));
                } else { //Loose
                    game.setTotalValue(Math.round(price.getLoosePrice()));
                }
            } else { //Just box/manual
                game.setTotalValue(0);
            }
        }

        gameDao.updateGame(game);
        return "redirect:/collection";
    }

    private int getTotalValue(List<Game> games) {
        int total = 0;
        for(Game g : games) {
            if(g.isNew()) {
                g.setTotalValue(Math.round(priceDao.getPriceByGameId(g.getId()).getNewPrice()));
            } else if(g.isComplete()) {
                g.setTotalValue(Math.round(priceDao.getPriceByGameId(g.getId()).getCibPrice()));
            } else if(g.isHaveGame()) {
                g.setTotalValue(Math.round(priceDao.getPriceByGameId(g.getId()).getLoosePrice()));
            } else {
                g.setTotalValue(0);
            }
            total += g.getTotalValue();
        }

        return total;
    }

    private void updateGamesAndPrices(List<Game> games) {
        if(games != null) {
            for (Game g : games) {
                Map<String, String> gameInfo = JSONFetcher.getGameInfoById(g.getId());
                Price price = new Price();
                price.setGameId(g.getId());
                price.setNewPrice(Float.parseFloat(gameInfo.get("newPrice")));
                price.setCibPrice(Float.parseFloat(gameInfo.get("cibPrice")));
                price.setLoosePrice(Float.parseFloat(gameInfo.get("loosePrice")));
                if (priceDao.getPriceByGameId(price.getGameId()) == null) {
                    priceDao.addPrice(price);
                } else {
                    priceDao.updatePrice(price);
                }

                g.setProductName(gameInfo.get("product-name"));
                g.setConsoleName(gameInfo.get("console-name"));
                g.setReleaseDate(gameInfo.get("releaseDate"));
                gameDao.updateGame(g);
            }
        }
    }

    private void populateConsoleList() {
        consoles.add(new Console("3D0"));
        consoles.add(new Console("Action Max"));
        consoles.add(new Console("Amiga"));
        consoles.add(new Console("Amiga CD32"));
        consoles.add(new Console("Amiibo"));
        consoles.add(new Console("Arcadia 2001"));
        consoles.add(new Console("Atari 2600"));
        consoles.add(new Console("Atari 400"));
        consoles.add(new Console("Atari 5200"));
        consoles.add(new Console("Atari 7800"));
        consoles.add(new Console("Atari Lynx"));
        consoles.add(new Console("Atari ST"));
        consoles.add(new Console("Bally Astrocade"));
        consoles.add(new Console("CD-i"));
        consoles.add(new Console("Colecovision"));
        consoles.add(new Console("Commodore 64"));
        consoles.add(new Console("Disney Infinity"));
        consoles.add(new Console("Evercade"));
        consoles.add(new Console("Fairchild Channel F"));
        consoles.add(new Console("Famicom"));
        consoles.add(new Console("Famicom Disk System"));
        consoles.add(new Console("Game & Watch"));
        consoles.add(new Console("Game.Com"));
        consoles.add(new Console("GameBoy"));
        consoles.add(new Console("GameBoy Advance"));
        consoles.add(new Console("GameBoy Color"));
        consoles.add(new Console("Gamecube"));
        consoles.add(new Console("Gizmondo"));
        consoles.add(new Console("Intellivision"));
        consoles.add(new Console("JP GameBoy"));
        consoles.add(new Console("JP GameBoy Advance"));
        consoles.add(new Console("JP GameBoy Color"));
        consoles.add(new Console("JP Gamecube"));
        consoles.add(new Console("JP MSX"));
        consoles.add(new Console("JP MSX2"));
        consoles.add(new Console("JP Neo Geo"));
        consoles.add(new Console("JP Neo Geo AES"));
        consoles.add(new Console("JP Neo Geo Pocket Color"));
        consoles.add(new Console("JP Nintendo 3DS"));
        consoles.add(new Console("JP Nintendo 64"));
        consoles.add(new Console("JP Nintendo DS"));
        consoles.add(new Console("JP Nintendo Switch"));
        consoles.add(new Console("JP PC Engine"));
        consoles.add(new Console("JP PC Engine CD"));
        consoles.add(new Console("JP PSP"));
        consoles.add(new Console("JP Playstation"));
        consoles.add(new Console("JP Playstation 2"));
        consoles.add(new Console("JP Playstation 3"));
        consoles.add(new Console("JP Playstation 4"));
        consoles.add(new Console("JP Playstation 5"));
        consoles.add(new Console("JP Playstation Vita"));
        consoles.add(new Console("JP Sega Dreamcast"));
        consoles.add(new Console("JP Sega Game Gear"));
        consoles.add(new Console("JP Sega Mark III"));
        consoles.add(new Console("JP Sega Mega CD"));
        consoles.add(new Console("JP Sega Mega Drive"));
        consoles.add(new Console("JP Sega Pico"));
        consoles.add(new Console("JP Sega Saturn"));
        consoles.add(new Console("JP Super 32X"));
        consoles.add(new Console("JP Virtual Boy"));
        consoles.add(new Console("JP Wii"));
        consoles.add(new Console("JP Wii U"));
        consoles.add(new Console("JP Xbox"));
        consoles.add(new Console("JP Xbox 360"));
        consoles.add(new Console("JP Xbox One"));
        consoles.add(new Console("JP Xbox Series X"));
        consoles.add(new Console("Jaguar"));
        consoles.add(new Console("Lego Dimensions"));
        consoles.add(new Console("Magnavox Odyssey"));
        consoles.add(new Console("Magnavox Odyssey 2"));
        consoles.add(new Console("Magnavox Odyssey 300"));
        consoles.add(new Console("Mattel Aquarius"));
        consoles.add(new Console("Microvision"));
        consoles.add(new Console("Mini Arcade"));
        consoles.add(new Console("N-Gage"));
        consoles.add(new Console("NES"));
        consoles.add(new Console("Neo Geo"));
        consoles.add(new Console("Neo Geo AES"));
        consoles.add(new Console("Neo Geo CD"));
        consoles.add(new Console("Neo Geo Pocket Color"));
        consoles.add(new Console("Nintendo 3DS"));
        consoles.add(new Console("Nintendo 64"));
        consoles.add(new Console("Nintendo DS"));
        consoles.add(new Console("Nintendo Power"));
        consoles.add(new Console("Nintendo Switch"));
        consoles.add(new Console("PAL Amiga CD32"));
        consoles.add(new Console("PAL GameBoy"));
        consoles.add(new Console("PAL GameBoy Advance"));
        consoles.add(new Console("PAL GameBoy Color"));
        consoles.add(new Console("PAL Gamecube"));
        consoles.add(new Console("PAL MSX"));
        consoles.add(new Console("PAL MSX2"));
        consoles.add(new Console("PAL Mega Drive 32X"));
        consoles.add(new Console("PAL NES"));
        consoles.add(new Console("PAL Neo Geo Pocket Color"));
        consoles.add(new Console("PAL Nintendo 3DS"));
        consoles.add(new Console("PAL Nintendo 64"));
        consoles.add(new Console("PAL Nintendo DS"));
        consoles.add(new Console("PAL Nintendo Switch"));
        consoles.add(new Console("PAL PSP"));
        consoles.add(new Console("PAL Playstation"));
        consoles.add(new Console("PAL Playstation 2"));
        consoles.add(new Console("PAL Playstation 3"));
        consoles.add(new Console("PAL Playstation 4"));
        consoles.add(new Console("PAL Playstation 5"));
        consoles.add(new Console("PAL Playstation Vita"));
        consoles.add(new Console("PAL Sega Dreamcast"));
        consoles.add(new Console("PAL Sega Game Gear"));
        consoles.add(new Console("PAL Sega Master System"));
        consoles.add(new Console("PAL Sega Mega CD"));
        consoles.add(new Console("PAL Sega Mega Drive"));
        consoles.add(new Console("PAL Sega Pico"));
        consoles.add(new Console("PAL Sega Saturn"));
        consoles.add(new Console("PAL Super Nintendo"));
        consoles.add(new Console("PAL Vectrex"));
        consoles.add(new Console("PAL Videopac G7000"));
        consoles.add(new Console("PAL Videopac G7400"));
        consoles.add(new Console("PAL Wii"));
        consoles.add(new Console("PAL Wii U"));
        consoles.add(new Console("PAL Xbox"));
        consoles.add(new Console("PAL Xbox 360"));
        consoles.add(new Console("PAL Xbox One"));
        consoles.add(new Console("PAL Xbox Series X"));
        consoles.add(new Console("PC FX"));
        consoles.add(new Console("PC Games"));
        consoles.add(new Console("PSP"));
        consoles.add(new Console("Playstation"));
        consoles.add(new Console("Playstation 2"));
        consoles.add(new Console("Playstation 3"));
        consoles.add(new Console("Playstation 4"));
        consoles.add(new Console("Playstation 5"));
        consoles.add(new Console("Playstation Vita"));
        consoles.add(new Console("Pokemon Mini"));
        consoles.add(new Console("Sega 32X"));
        consoles.add(new Console("Sega CD"));
        consoles.add(new Console("Sega Dreamcast"));
        consoles.add(new Console("Sega Game Gear"));
        consoles.add(new Console("Sega Genesis"));
        consoles.add(new Console("Sega Master System"));
        consoles.add(new Console("Sega Pico"));
        consoles.add(new Console("Sega Saturn"));
        consoles.add(new Console("Skylanders"));
        consoles.add(new Console("Strategy Guide"));
        consoles.add(new Console("Super Famicom"));
        consoles.add(new Console("Super Nintendo"));
        consoles.add(new Console("TI-99"));
        consoles.add(new Console("TurboGrafx CD"));
        consoles.add(new Console("TurboGrafx-16"));
        consoles.add(new Console("Vectrex"));
        consoles.add(new Console("Vic-20"));
        consoles.add(new Console("Virtual Boy"));
        consoles.add(new Console("Wholesale"));
        consoles.add(new Console("Wii"));
        consoles.add(new Console("Wii U"));
        consoles.add(new Console("WonderSwan"));
        consoles.add(new Console("WonderSwan Color"));
        consoles.add(new Console("Xbox"));
        consoles.add(new Console("Xbox 360"));
        consoles.add(new Console("Xbox One"));
        consoles.add(new Console("Xbox Series X"));
    }
}
