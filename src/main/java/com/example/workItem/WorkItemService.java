package com.example.workItem;

import com.example.sign.Sign;
import com.example.sign.SignMapper;
import com.example.sign.SignRepository;
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
    private SignRepository signRepository;
    @Autowired
    private SignMapper signMapper;


    @Transactional
    public WorkItemDto inviteUserToWork(Long workId, UserDto userDto, Long signId) {
        // Step 1: Find the work entity by ID
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new IllegalArgumentException("Work not found"));

        // Step 2: Find or create the user using UserService
        User user = userService.findOrCreateUser(userDto);

        // Step 3: Create the WorkItem for the existing or newly created user
        WorkItem workItem = new WorkItem();
        workItem.setWork(work);
        workItem.setUser(user); // Assign the non-null User
        workItem.setType(1); //Default type


        // Ensure the 'type' field is set to a non-null value
        workItem.setType(1); // Set this to a default type, or derive it from your business logic.

        // Set the sign if it is not null
        if(signId != null && signId > 0) {
            Sign sign = signRepository.findById(signId)
                    .orElseThrow(() -> new IllegalArgumentException("Sign not found"));
            workItem.setSign(sign);
        }
        // Save the WorkItem to the repository
        workItemRepository.save(workItem);
        // 작업 공유 상태 업데이트
//        workService.updateWorkSharedStatus(workId, user.getId());
        // Return the WorkItemDto
        return workItemMapper.toDto(workItem);
    }
    @Transactional
    public WorkItem createWorkItem(WorkItemDto workItemDto, Long workId, Long userId, Long signId) {
        // Find the work entity by ID
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new IllegalArgumentException("Work not found with id: " + workId));

        // Find the user entity by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // Create a new WorkItem entity
        WorkItem workItem = new WorkItem();
        workItem.setWork(work);
        workItem.setUser(user);

        // If signId is provided and valid, associate the sign with the WorkItem
        if (signId != null && signId > 0) {
            Sign sign = signRepository.findById(signId)
                    .orElseThrow(() -> new IllegalArgumentException("Sign not found with id: " + signId));
            workItem.setSign(sign);
        } else {
            workItem.setSign(null); // or handle default logic
        }

        // Save the WorkItem to the repository
        return workItemRepository.save(workItem);
    }


    public List<WorkItemDto> findByWorkId(Long workId) {
        List<WorkItem> workItems = workItemRepository.findByWorkId(workId);

        return workItems.stream()
                .map(workItem -> {
                    WorkItemDto dto = workItemMapper.toDto(workItem);
                    if (workItem.getSign() == null) {
                        dto.setSign(null); // signId가 null이면 sign을 null로 설정
                    } else {
                       dto.setSign(signMapper.toDto(workItem.getSign()));//signId가 존재하면 sign을 가져와서 설정
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public WorkItem createDefaultWorkItemForCreator(Work work, User creator) { //작업 생성자를 위한 기본 작업 항목 생성
        WorkItem workItem = new WorkItem(work, creator);
        workItem.setType(1);
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
        //WorkItem 엔티티를 찾아서 가져온다.
        WorkItem workItem = workItemRepository.findById(workItemId)
                .orElseThrow(() -> new IllegalArgumentException("WorkItem not found"));
        //Sign이 존재하면 Sign을 가져와서 WorkItem에 설정한다.
        if (workItemDto.getSignId()!= null && workItemDto.getSignId()>0){
            Sign sign = signRepository.findById(workItemDto.getSignId()).orElseThrow(()-> new IllegalArgumentException("Sign not found with id : "+ workItemDto.getSignId()));
            workItem.setSign(sign);
        }else{//Sign이 없으면 null로 설정
            workItem.setSign(null);
        }
        //WorkItemDto의 필드를 업데이트한다.
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

        return workItemMapper.toDto(updatedWorkItem);
    }


@Transactional
public void deleteWorkItem(Long workItemId) {
    WorkItem workItem = workItemRepository.findById(workItemId)
            .orElseThrow(() -> new IllegalArgumentException("WorkItem not found"));
    workItemRepository.delete(workItem);
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

    }

//    @Transactional
//    public WorkItemDto getWorkItemById(Long workItemId) {
//        WorkItem workItem = workItemRepository.findById(workItemId)
//                .orElseThrow(() -> new IllegalArgumentException("WorkItem not found with id: " + workItemId));
//        return workItemMapper.toDto(workItem);
//    }


}
