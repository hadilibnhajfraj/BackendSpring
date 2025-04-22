package tn.esprit.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.MatchFo;
import tn.esprit.spring.services.Iservice;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private Iservice iservice;

    @PostMapping("/addMatch")
    public ResponseEntity<MatchFo> ajouterMatch(@RequestBody MatchFo matchFo) {
        return ResponseEntity.ok(iservice.ajouterMatch(matchFo));
    }

    @PutMapping("/updateMatch/{id}")
    public ResponseEntity<MatchFo> updateMatch(@PathVariable Integer id, @RequestBody MatchFo matchFo) {
        return ResponseEntity.ok(iservice.updateMatch(id, matchFo));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MatchFo>> getAllMatchs() {
        return ResponseEntity.ok(iservice.getAllMatch());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchFo> getMatchById(@PathVariable Integer id) {
        return iservice.getMatchId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deleteMatch/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Integer id) {
        iservice.deleteMatch(id);
        return ResponseEntity.noContent().build();
    }
}
