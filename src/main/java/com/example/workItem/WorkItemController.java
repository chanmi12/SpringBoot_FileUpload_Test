package com.example.workItem;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.user.User;
import com.example.work.entity.Work;
import java.util.List;

@RestController
@RequestMapping("/api/{userId}/{workId}/work")
public class WorkItemController {

//    private final WorkItemService workItemService;
//
//    public WorkItemController(WorkItemService workItemService) {
//        this.workItemService = workItemService;
//    }
//
//    @PostMapping
//    public ResponseEntity<WorkItemDto> createWorkItem(@RequestBody WorkItemDto workItemDto,
//                                                      @PathVariable Long userId,
//                                                      @PathVariable Long workId) {
//        // Retrieve User and Work from their respective services/repositories.
//        User user = getUserById(userId); // Pseudo code
//        Work work = getWorkById(workId); // Pseudo code
//        WorkItemDto createdWorkItem = workItemService.createWorkItem(workItemDto, user, work);
//        return ResponseEntity.ok(createdWorkItem);
//    }
//
//    @GetMapping("/{workItemId}")
//    public ResponseEntity<WorkItemDto> getWorkItem(@PathVariable Long workItemId,
//                                                   @PathVariable Long userId,
//                                                   @PathVariable Long workId) {
//        // Retrieve User and Work from their respective services/repositories.
//        User user = getUserById(userId); // Pseudo code
//        Work work = getWorkById(workId); // Pseudo code
//        WorkItemDto workItem = workItemService.getWorkItem(workItemId, user, work);
//        return ResponseEntity.ok(workItem);
//    }
//
//    // Helper methods to fetch User and Work (you would need to implement these)
//    private User getUserById(Long userId) {
//        // Implement logic to fetch User
//    }
//
//    private Work getWorkById(Long workId) {
//        // Implement logic to fetch Work
//    }
}