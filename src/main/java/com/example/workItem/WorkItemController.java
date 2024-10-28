package com.example.workItem;

import com.example.sign.SignService;
import com.example.user.UserDto;
import com.example.user.UserService;
import com.example.work.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class WorkItemController {

    @Autowired
    private WorkItemService workItemService;
    @Autowired
    private UserService userService;
    @Autowired
    private WorkService workService;
    @Autowired
    private SignService signService;
@Autowired
private WorkItemMapper workItemMapper;
    //workId에 해당하는 work에 userDto를 초대
    @PostMapping("/{userId}/{workId}/workItem/invite")
    public ResponseEntity<WorkItemDto> inviteUserToWork(@PathVariable Long userId,
                                                        @PathVariable Long workId,
                                                        @RequestBody UserDto userDto,
                                                        @RequestParam(required = false) Long signId) {
        WorkItemDto workItemDto = workItemService.inviteUserToWork(workId, userDto,signId);
        return ResponseEntity.ok(workItemDto);
    }

    //workId에 해당하는 work에 속한 workItem 생성
    @PostMapping("/{userId}/{workId}/workItem")
    public ResponseEntity<WorkItem> createWorkItem(@PathVariable Long userId,
                                                   @RequestParam Long workId,
                                                   @RequestBody WorkItemDto workItemDto,
                                                    @RequestParam(required = false) Long signId) {
        WorkItem createWorkItem = workItemService.createWorkItem(workItemDto, workId, userId, signId);
        return ResponseEntity.ok(createWorkItem);
    }

    // 특정 Work의 모든 WorkItem 조회
    @GetMapping("/{userId}/{workId}/workItem")
    public ResponseEntity<List<WorkItemDto>> getAllWorkItemsForWork(@PathVariable Long workId) {
        List<WorkItemDto> workItems = workItemService.findByWorkId(workId);
        return ResponseEntity.ok(workItems);
    }
    @GetMapping("/{userId}/{workId}/workItem/{otherId}") //특정 Work에 속한 특정 User의 WorkItem 조회
//    public ResponseEntity<List<WorkItem>> getWorkItemsByWorkIdAndOtherUserId(@PathVariable Long workId, @PathVariable Long otherId) {
//        List<WorkItem> workItems = workItemService.getWorkItemsByWorkIdAndOtherUserId(workId, otherId);
//        return ResponseEntity.ok(workItems);
//    }
    public ResponseEntity<List<WorkItemDto>> getWorkItemByWorkIdAmdOtherUserId(@PathVariable Long workId, @PathVariable Long otherId) {
        //특정 Work에 속한 특정 User의 WorkItem 조회
        List<WorkItem> workItems = workItemService.getWorkItemsByWorkIdAndOtherUserId(workId, otherId);
        //Stream API를 사용하여 Entity를 Dto로 변환
        List<WorkItemDto> workItemDtos = workItems.stream()
                .map(workItemMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(workItemDtos);
    }

    @GetMapping("/{userId}/{workId}/workItem/users") //특정 Work에 속한 모든 User 조회
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

    @DeleteMapping("/{userId}/{workId}/workItems")
    public ResponseEntity<String> deleteWorkItemsForUserFromWork(
            @PathVariable Long userId,
            @PathVariable Long workId) {

        workItemService.deleteWorkItemsByUserAndWork(userId, workId);
        return ResponseEntity.ok("Work items deleted for the user in this work");
    }

//    @GetMapping("/{userId}/{workId}/workItem/{workItemId}")
//    public ResponseEntity<WorkItemDto> getWorkItem(@PathVariable Long workId, @PathVariable Long workItemId) {
//        WorkItemDto workItemDto = workItemService.getWorkItemById(workItemId);
//        return ResponseEntity.ok(workItemDto);
//    }

}