package uk.ac.ucl.rits.popchat;

import java.util.Set;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.ucl.rits.popchat.rhyming.Rhymes;

@RestController
public class Endpoints {

    @RequestMapping("words/rhymes-with/{word}")
    public Set<String> rhymesWith(@PathVariable String word){
      return Rhymes.getRhymes().rhymes(word.toLowerCase());
    }

}