package com.example.workItem;

import com.example.user.UserDto;
import com.example.user.UserRepository;
import com.example.user.UserService;
import com.example.work.repository.WorkRepository;

import com.example.work.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.user.User;
import com.example.work.entity.Work;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkItemService {

    @Autowired
    private WorkItemRepository workItemRepository;
    @Autowired
    private WorkRepository workRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WorkItemMapper workItemMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private WorkService workService;


    @Transactional
    public WorkItemDto inviteUserToWork(Long workId, UserDto userDto) {
        // Step 1: Find the work entity by ID
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new IllegalArgumentException("Work not found"));

        // Step 2: Find or create the user using UserService
        User user = userService.findOrCreateUser(userDto);

        // Step 3: Create the WorkItem for the existing or newly created user
        WorkItem workItem = new WorkItem();
        workItem.setWork(work);
        workItem.setUser(user); // Assign the non-null User
        workItem.setSignId(0L);

        // Ensure the 'type' field is set to a non-null value
        workItem.setType(1); // Set this to a default type, or derive it from your business logic.

        // Save the WorkItem to the repository
        workItemRepository.save(workItem);
        // 작업 공유 상태 업데이트
        workService.updateWorkSharedStatus(workId, user.getId());

        // Return the WorkItemDto
        return workItemMapper.toDto(workItem);
    }
    public WorkItem createWorkItem(WorkItemDto workItemDto, Long workId, Long userId) {
        // Retrieve Work and User from their respective services or repositories
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new RuntimeException("Work not found with id: " + workId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Call toEntity with the required parameters
        WorkItem workItem = workItemMapper.toEntity(workItemDto, work, user);

        WorkItem savedWorkItem = workItemRepository.save(workItem);
        // 작업 공유 상태 업데이트
        workService.updateWorkSharedStatus(workId, userId);

        // Save the workItem
        return savedWorkItem;
    }


    public List<WorkItemDto> findByWorkId(Long workId){ //특정 작업에 대한 모든 작업 항목 가져오기
        // Step 1: Find all WorkItems by workId
        List<WorkItem> workItems = workItemRepository.findByWorkId(workId);
        return workItems.stream()
                .map(workItemMapper :: toDto)
                .collect(Collectors.toList());
    }

    public WorkItem createDefaultWorkItemForCreator(Work work, User creator) { //작업 생성자를 위한 기본 작업 항목 생성
        WorkItem workItem = new WorkItem(work, creator);
        // workItem.setSignId(0L);
        workItem.setType(2);
        return workItemRepository.save(workItem);
    }

    public List<UserDto> listUniqueUsersForWork(Long workId) {
        // Find all WorkItems for the specified workId
        List<WorkItem> workItems = workItemRepository.findByWorkId(workId);

        // Map the WorkItem list to users, ensure no duplicates
        return workItems.stream()
                .map(WorkItem::getUser) // Extract user from each WorkItem
                .distinct() // Ensure distinct users
                .map(user -> new UserDto(user.getId(), user.getName(), user.getEmail())) // Convert User to UserDto
                .collect(Collectors.toList()); // Collect as a list of UserDto
    }

    public WorkItemDto updateWorkItem(Long workItemId, WorkItemDto workItemDto){ //ID로 작업 항목 업데이트
        //Find the WorkItem by ID
        WorkItem workItem = workItemRepository.findById(workItemId)
                .orElseThrow(() -> new IllegalArgumentException("WorkItem not found"));
        //Update the WorkItem with the new values
        workItem.setSignId(workItemDto.getSignId());
        workItem.setType(workItemDto.getType());
        workItem.setText(workItemDto.getText());
        workItem.setXPosition(workItemDto.getXPosition());
        workItem.setYPosition(workItemDto.getYPosition());
        workItem.setWidth(workItemDto.getWidth());
        workItem.setHeight(workItemDto.getHeight());
        workItem.setFree(workItemDto.getFree());
        workItem.setPage(workItemDto.getPage());
        workItem.setFontSize(workItemDto.getFontSize());
        workItem.setFontStyle(workItemDto.getFontStyle());
        //Save the updated WorkItem
       WorkItem updatedWorkItem = workItemRepository.save(workItem);
        // 작업 공유 상태 업데이트
       workService.updateWorkSharedStatus(workItem.getWork().getId(), workItem.getUser().getId());
       //Return the updated WorkItemDto
        return workItemMapper.toDto(updatedWorkItem);
    }

    //이전 삭제 버전
//    public void deleteWorkItem(Long workItemId){  //ID로 작업 항목 삭제
//
//         if (!workItemRepository.existsById(workItemId)){
//            throw new IllegalArgumentException("WorkItem not found");
//        }
//         workItemRepository.deleteById(workItemId);
//
//    }

@Transactional
public void deleteWorkItem(Long workItemId) {
    WorkItem workItem = workItemRepository.findById(workItemId)
            .orElseThrow(() -> new IllegalArgumentException("WorkItem not found"));

    workItemRepository.delete(workItem);

    // 작업 공유 상태 업데이트
    workService.updateWorkSharedStatus(workItem.getWork().getId(), workItem.getUser().getId());
}

    public List<WorkItem> getWorkItemsByWorkIdAndOtherUserId(Long workId, Long otherId) {//특정 작업에 대한 특정 사용자의 모든 작업 항목 가져오기
        return workItemRepository.findByWorkIdAndOtherUserId(workId, otherId);

    }

    public void deleteWorkItemsByUserAndWork(Long userId, Long workId) {
        List<WorkItem> workItems = workItemRepository.findByUserIdAndWorkId(userId, workId);

        if (workItems.isEmpty()) {
            throw new IllegalArgumentException("No WorkItems found for the user in this work.");
        }
        workItemRepository.deleteAll(workItems);

        // 작업 공유 상태 업데이트
        workService.updateWorkSharedStatus(workId, userId);
    }

}
