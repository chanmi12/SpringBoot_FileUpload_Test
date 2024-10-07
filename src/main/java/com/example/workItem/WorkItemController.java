package com.example.workItem;

import com.example.user.UserDto;
import com.example.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class WorkItemController {

    @Autowired
    private WorkItemService workItemService;
    @Autowired
    private UserService userService;

    //workId에 해당하는 work에 userDto를 초대
    @PostMapping("/{userId}/{workId}/workItem/invite")
    public ResponseEntity<WorkItemDto> inviteUserToWork(@PathVariable Long workId, @RequestBody UserDto userDto) {
        WorkItemDto workItemDto = workItemService.inviteUserToWork(workId, userDto);
        return ResponseEntity.ok(workItemDto);
    }

    //workId에 해당하는 work에 속한 workItem 생성
    @PostMapping("/{userId}/{workId}/workItem")
    public ResponseEntity<WorkItem> createWorkItem(@PathVariable Long userId, @PathVariable Long workId, @RequestBody WorkItemDto workItemDto) {
        WorkItem workItem = workItemService.createWorkItem(workItemDto, workId, userId);
        return ResponseEntity.ok(workItem);
    }

    // 특정 Work의 모든 WorkItem 조회
    @GetMapping("/{userId}/{workId}/workItem")
    public ResponseEntity<List<WorkItemDto>> getAllWorkItemsForWork(@PathVariable Long workId) {
        List<WorkItemDto> workItems = workItemService.findByWorkId(workId);
        return ResponseEntity.ok(workItems);
    }
    @GetMapping("/{userId}/{workId}/workItem/{otherId}") //특정 Work에 속한 특정 User의 WorkItem 조회
    public ResponseEntity<List<WorkItem>> getWorkItemsByWorkIdAndOtherUserId(@PathVariable Long workId, @PathVariable Long otherId) {
        List<WorkItem> workItems = workItemService.getWorkItemsByWorkIdAndOtherUserId(workId, otherId);
        return ResponseEntity.ok(workItems);
    }

    @GetMapping("/{userId}/{workId}/workItem/users")
    public ResponseEntity<List<UserDto>> getUsersForWork(@PathVariable Long workId) {
        List<UserDto> users = workItemService.listUniqueUsersForWork(workId);
        return ResponseEntity.ok(users);
    }
    @PutMapping("/{userId}/{workId}/workItem/{workItemId}") //WorkItem 수정
    public ResponseEntity<WorkItemDto> updateWorkItem(@PathVariable Long workItemId, @RequestBody WorkItemDto workItemDto) {
        WorkItemDto updatedWorkItem = workItemService.updateWorkItem(workItemId, workItemDto);
        return ResponseEntity.ok(updatedWorkItem);
    }

    //workId에 해당하는 work에 속한 workItem 삭제
    @DeleteMapping("/{userId}/{workId}/workItem/{workItemId}")
    public ResponseEntity<Void> deleteWorkItem(@PathVariable Long workItemId) {
        workItemService.deleteWorkItem(workItemId);
        return ResponseEntity.noContent().build();
    }


}