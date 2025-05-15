package com.bearhive.bearhive.Controller;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.bearhive.bearhive.Model.Flashcard;
import com.bearhive.bearhive.Model.Test;
import com.bearhive.bearhive.Model.User;
import com.bearhive.bearhive.Model.Word;
import com.bearhive.bearhive.Repository.FlashcardRepository;
import com.bearhive.bearhive.Repository.TestRepository;
import com.bearhive.bearhive.Repository.UserRepository;
import com.bearhive.bearhive.Repository.WordRepository;




@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private FlashcardRepository flashcardRepository;

    @Autowired
    private WordRepository wordRepository;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activePage", "dashboard");
        return "admininfo";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("activePage", "users");
        List<User> users = userRepository.findByRole("USER");
        model.addAttribute("users", users);
        return "admininfo";
    }

    @GetMapping("/tests")
    public String tests(Model model) {
        model.addAttribute("activePage", "tests");
        model.addAttribute("subPage", "test-list");
        Optional<User> user = userRepository.findByEmail("thuyanh5404.kba@gmail.com");
        List<Test> tests;
        if (user.isPresent()) {
            tests = testRepository.findByUserId(user.get().getId());
        } else {
            tests = List.of();
            model.addAttribute("error", "Lỗi không thấy email ADMIN");
        }
        model.addAttribute("tests", tests);
        return "admininfo";
    }
    
    @GetMapping("/tests/create")
    public String createTest(Model model) {
        model.addAttribute("activePage", "tests");
        model.addAttribute("subPage", "test-create");
        model.addAttribute("test", new Test());
        return "admininfo";
    }
    
    @PostMapping("/tests/create")
    public String createTestSubmit(@ModelAttribute("test") Test test, @RequestParam("imageFile") MultipartFile imageFile, Model model) {
        try {
            Optional<User> userOp = userRepository.findByEmail("thuyanh5404.kba@gmail.com");
            if (userOp.isPresent()) {
                User user = userOp.get();
                test.setUser(user);
            }
            else {
                model.addAttribute("error", "Lỗi không thấy email ADMIN");
            }
            if (test.getUpdateDate()==null) {
                test.setUpdateDate(LocalDate.now());
            }
            String fileName = imageFile.getOriginalFilename();
            String upDir = Paths.get("bearhive/src/main/resources/static/image/test").toAbsolutePath().toString();
            File dir = new File(upDir);
            if (!dir.exists()) {
                dir.mkdir();
            } 
            imageFile.transferTo(new File(dir, fileName));
            test.setCoveredImage("/image/test/" + fileName);
            testRepository.save(test);
            return "redirect:/admin/tests";
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            model.addAttribute("error", "Lỗi khi tạo test");
            model.addAttribute("activePage", "tests");
            model.addAttribute("subPage", "test-create");
            return "admininfo";
        }
    }
    
    @GetMapping("/flashcards")
    public String flashcards(Model model) {
        model.addAttribute("activePage", "flashcards");
        model.addAttribute("subPage", "flashcard-list");
        Optional<User> user = userRepository.findByEmail("thuyanh5404.kba@gmail.com");
        List<Flashcard> flashcards;
        if (user.isPresent()) {
            flashcards = flashcardRepository.findByUserId(user.get().getId());
        } else {
            flashcards = List.of();
            model.addAttribute("error", "Lỗi không thấy email ADMIN");
        }
        model.addAttribute("flashcards", flashcards);
        return "admininfo";
    }
    
    @GetMapping("/flashcards/create")
    public String createFlashcard(Model model) {
        model.addAttribute("activePage", "flashcards");
        model.addAttribute("subPage", "flashcard-create");
        model.addAttribute("flashcard", new Flashcard());
        return "admininfo";
    }

    @PostMapping("/flashcards/create")
    public String createFlashcardSubmit(@ModelAttribute("flashcard") Flashcard flashcard, @RequestParam("imageFile") MultipartFile imageFile, Model model) {
        try {
            Optional<User> userOp = userRepository.findByEmail("thuyanh5404.kba@gmail.com");
            if (userOp.isPresent()) {
                User user = userOp.get();
                flashcard.setUser(user);
            } else {
                model.addAttribute("error", "Lỗi không thấy email ADMIN");
            }
            if (flashcard.getUpdateDate() == null) {
                flashcard.setUpdateDate(LocalDate.now());
            }
            flashcardRepository.save(flashcard);
            Long flashcardId = flashcard.getId(); 
            String upDir = Paths.get("bearhive/src/main/resources/static/image/flashcard/" + flashcardId).toAbsolutePath().toString();
            File dir = new File(upDir);
            if (!dir.exists()) {
                dir.mkdirs(); 
            }
            String fileName = imageFile.getOriginalFilename();
            if (!imageFile.isEmpty()) {
                imageFile.transferTo(new File(dir, fileName));
                flashcard.setCoveredImage("/image/flashcard/" + flashcardId + "/" + fileName);
            }
            flashcardRepository.save(flashcard);
            
            return "redirect:/admin/flashcards";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("error", "Lỗi khi tạo flashcard");
            model.addAttribute("activePage", "flashcards");
            model.addAttribute("subPage", "flashcard-create");
            return "admininfo";
        }
    }

    @PostMapping("/flashcards/delete")
    public String deleteFlashcard(@RequestParam("id") Long id, Model model) {
        try {
            flashcardRepository.deleteById(id);
            return "redirect:/admin/flashcards";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi xóa flashcard");
            model.addAttribute("activePage", "flashcards");
            model.addAttribute("subPage", "flashcard-list");
            return "adminflashcard";
        }
    }

    @PostMapping("/flashcards/edit")
    public String editFlashcardSubmit(@ModelAttribute("flashcard") Flashcard flashcard, @RequestParam("imageFile") MultipartFile imageFile, Model model) {
        try {
            Optional<Flashcard> existingFlashcard = flashcardRepository.findById(flashcard.getId());
            if (existingFlashcard.isPresent()) {
                Flashcard updatedFlashcard = existingFlashcard.get();
                updatedFlashcard.setTitle(flashcard.getTitle());
                updatedFlashcard.setType(flashcard.getType());
                updatedFlashcard.setDescription(flashcard.getDescription());
                updatedFlashcard.setDifficulty(flashcard.getDifficulty());
                updatedFlashcard.setQuestions(flashcard.getQuestions());
                updatedFlashcard.setUpdateDate(LocalDate.now());

                if (!imageFile.isEmpty()) {
                    String fileName = imageFile.getOriginalFilename();
                    String upDir = Paths.get("bearhive/src/main/resources/static/image/flashcard/" + flashcard.getId()).toAbsolutePath().toString();
                    File dir = new File(upDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    imageFile.transferTo(new File(dir, fileName));
                    updatedFlashcard.setCoveredImage("/image/flashcard/" + flashcard.getId() + "/" + fileName);
                }
                flashcardRepository.save(updatedFlashcard);
            }
            return "redirect:/admin/flashcards";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi sửa flashcard");
            model.addAttribute("activePage", "flashcards");
            model.addAttribute("subPage", "flashcard-list");
            return "adminflashcard";
        }
    }

    @GetMapping("/flashcards/{id}/words")
    public String viewFlashcardWords(@PathVariable("id") Long id, Model model) {
        Optional<Flashcard> flashcard = flashcardRepository.findById(id);
        if (flashcard.isPresent()) {
            model.addAttribute("flashcard", flashcard.get());
            model.addAttribute("words", wordRepository.findByFlashcardId(id));
            model.addAttribute("activePage", "flashcards");
            model.addAttribute("subPage", "flashcard-words");
            return "admininfo";
        } else {
            model.addAttribute("error", "Flashcard không tìm thấy");
            return "redirect:/admin/flashcards";
        }
    }

    @PostMapping("/flashcards/{id}/words/create")
    public String createWord(@PathVariable("id") Long flashcardId, @ModelAttribute("newWord") Word word, @RequestParam("imageFile") MultipartFile imageFile, Model model) {
        try {
            Optional<Flashcard> flashcard = flashcardRepository.findById(flashcardId);
            if (flashcard.isPresent()) {
                if (word.getId() != null) {
                    word.setId(null); 
                }
                word.setFlashcard(flashcard.get());
                if (!imageFile.isEmpty()) {
                    String fileName = imageFile.getOriginalFilename();
                    String upDir = Paths.get("bearhive/src/main/resources/static/image/flashcard/" + flashcardId).toAbsolutePath().toString();
                    File dir = new File(upDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    imageFile.transferTo(new File(dir, fileName));
                    word.setImage("/image/flashcard/" + flashcardId + "/" + fileName);
                    wordRepository.save(word);
                }
            }
            return "redirect:/admin/flashcards/" + flashcardId + "/words";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tạo word: " + e.getMessage());
            return "redirect:/admin/flashcards/" + flashcardId + "/words";
        }
    }


    @PostMapping("/flashcards/{id}/words/edit")
    public String editWord(@PathVariable("id") Long flashcardId, @ModelAttribute("newWord") Word word, @RequestParam("imageFile") MultipartFile imageFile, Model model) {
        try {
            Optional<Word> existingWord = wordRepository.findById(word.getId());
            if (existingWord.isPresent()) {
                Word updatedWord = existingWord.get();
                updatedWord.setWord(word.getWord());
                updatedWord.setPronunciation(word.getPronunciation());
                updatedWord.setType(word.getType());
                updatedWord.setMeaning(word.getMeaning());
                updatedWord.setExample(word.getExample());
                if (!imageFile.isEmpty()) {
                    String fileName = imageFile.getOriginalFilename();
                    String upDir = Paths.get("bearhive/src/main/resources/static/image/flashcard/" + flashcardId).toAbsolutePath().toString();
                    File dir = new File(upDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    imageFile.transferTo(new File(dir, fileName));
                    updatedWord.setImage("/image/flashcard/" + flashcardId + "/" + fileName);
                }
                wordRepository.save(updatedWord);
            }
            return "redirect:/admin/flashcards/" + flashcardId + "/words";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi sửa word: " + e.getMessage());
            return "redirect:/admin/flashcards/" + flashcardId + "/words";
        }
    }

    @PostMapping("/flashcards/{id}/words/delete")
    public String deleteWord(@PathVariable("id") Long flashcardId, @RequestParam("id") Long wordId, Model model) {
        try {
            wordRepository.deleteById(wordId);
            return "redirect:/admin/flashcards/" + flashcardId + "/words";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi xóa word");
            return "redirect:/admin/flashcards/" + flashcardId + "/words";
        }
    }
}
