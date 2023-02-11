package controllers;
import Lemma.LemmaFinder;
import jakarta.annotation.PostConstruct;
import main.repositories.LemmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;


import java.io.IOException;
import java.util.HashMap;

@Component
@Controller
public class DataLoader {
    private static LemmaRepository lemmaRepository;
    private LemmaFinder lemmaFinder = new LemmaFinder();
    private final HashMap<String, Integer> lemmasFrequency = new HashMap<>();


    @Autowired
    public DataLoader(LemmaRepository lemmaRepository) throws IOException {
        this.lemmaRepository = lemmaRepository;
    }

    @PostConstruct
    public void saveLemmas() throws IOException {
      lemmaFinder.getBodyLemmasFromEachPage(lemmasFrequency);
    }

    public static void get() {
        System.out.println(lemmaRepository.findAll());
    }
}

