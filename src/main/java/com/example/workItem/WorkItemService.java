package com.example.workItem;

import com.example.sign.Sign;
import com.example.sign.SignDto;
import com.example.sign.SignMapper;
import com.example.sign.SignRepository;
import com.example.user.UserDto;
import com.example.user.UserRepository;
import com.example.user.UserService;
import com.example.work.WorkRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.user.User;
import com.example.work.Work;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private SignRepository signRepository;

    @Autowired
    private WorkItemMapper workItemMapper;
    @Autowired
    private UserService userService;
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
        workItem.setAutoCreated(true); // Set autoCreated to true
        workItem.setFinished(false); // Set finished to false
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
    public WorkItem createWorkItem(WorkItemDto workItemDto, Long workId, Long userId, Long targetUserId) {
        // Retrieve Work and User entities
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new IllegalArgumentException("Work not found with id: " + workId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target User not found with id: " + targetUserId));

        // Retrieve Sign if signId is provided
        Sign sign = null;
        if (workItemDto.getSignId() != null) {
            sign = signRepository.findById(workItemDto.getSignId())
                    .orElseThrow(() -> new IllegalArgumentException("Sign not found with id: " + workItemDto.getSignId()));
        }

        // Create and set WorkItem properties
        WorkItem workItem = new WorkItem();
        workItem.setWork(work);
        workItem.setUser(targetUser);
        workItem.setAutoCreated(false); // Set autoCreated to false\
        workItem.setFinished(false); // Set finished to false
        // Handle each case individually
        switch (workItemDto.getType()) {
            case 1: // General Signature
                workItem.setType(1);
                workItem.setSign(sign); // Set sign if provided
                workItem.setXPosition(workItemDto.getXPosition());
                workItem.setYPosition(workItemDto.getYPosition());
                workItem.setWidth(workItemDto.getWidth());
                workItem.setHeight(workItemDto.getHeight());
                workItem.setFree(false); // Not free mode
                workItem.setPage(workItemDto.getPage());
                break;

            case 2: // General Text
                workItem.setType(2);
                workItem.setText(workItemDto.getText());
                workItem.setXPosition(workItemDto.getXPosition());
                workItem.setYPosition(workItemDto.getYPosition());
                workItem.setWidth(workItemDto.getWidth());
                workItem.setHeight(workItemDto.getHeight());
                workItem.setFree(false); // Not free mode
                workItem.setPage(workItemDto.getPage());
                workItem.setFontSize(workItemDto.getFontSize());
                workItem.setFontStyle(workItemDto.getFontStyle());
                break;

            case 3: // Free Signature
                workItem.setType(3); // Same as signature but with free mode
                workItem.setSign(sign); // Set sign if provided
                workItem.setXPosition(workItemDto.getXPosition());
                workItem.setYPosition(workItemDto.getYPosition());
                workItem.setWidth(workItemDto.getWidth());
                workItem.setHeight(workItemDto.getHeight());
                workItem.setFree(true); // Enable free mode
                workItem.setPage(workItemDto.getPage());
                break;

            case 4: // Free Text
                workItem.setType(4); // Same as text but with free mode
                workItem.setText(workItemDto.getText());
                workItem.setXPosition(workItemDto.getXPosition());
                workItem.setYPosition(workItemDto.getYPosition());
                workItem.setWidth(workItemDto.getWidth());
                workItem.setHeight(workItemDto.getHeight());
                workItem.setFree(true); // Enable free mode
                workItem.setPage(workItemDto.getPage());
                workItem.setFontSize(workItemDto.getFontSize());
                workItem.setFontStyle(workItemDto.getFontStyle());
                break;

            default:
                throw new IllegalArgumentException("Invalid type value: " + workItemDto.getType());
        }
        // Save the WorkItem
        return workItemRepository.save(workItem);
    }

    @Transactional
    public List<WorkItemDto> findByWorkId(Long workId) {
        List<WorkItem> workItems = workItemRepository.findByWorkId(workId);

        return workItems.stream()
                .map(workItem -> {
                    WorkItemDto dto = workItemMapper.toDto(workItem);

                    // signId가 null이 아니면 Sign 정보를 DTO에 추가
                    if (workItem.getSign() != null) {
                        SignDto signDto = signMapper.toDto(workItem.getSign());
                        dto.setSign(signDto);
                    } else {
                        dto.setSign(null);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
    @Transactional
    public WorkItem createDefaultWorkItemForCreator(Work work, User creator) { //작업 생성자를 위한 기본 작업 항목 생성
        WorkItem workItem = new WorkItem(work, creator);
        workItem.setType(1);
        return workItemRepository.save(workItem);
    }

    @Transactional
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
@Transactional
    public WorkItemDto updateWorkItem(Long workItemId, WorkItemDto workItemDto){ //ID로 작업 항목 업데이트
        //WorkItem 엔티티를 찾아서 가져온다.
        WorkItem workItem = workItemRepository.findById(workItemId)
                .orElseThrow(() -> new IllegalArgumentException("WorkItem not found"));
        //Sign이 존재하면 Sign을 가져와서 WorkItem에 설정한다.
        if (workItemDto.getSignId() != null && workItemDto.getSignId() > 0) {
            Sign sign = signRepository.findById(workItemDto.getSignId())
                    .orElseThrow(() -> new IllegalArgumentException("Sign not found with id: " + workItemDto.getSignId()));
            workItem.setSign(sign);
        }else{//Sign이 없으면 null로 설정
            workItem.setSign(null);
        }

        //WorkItemDto의 필드를 업데이트한다.
        if(workItemDto.getType() != null){
            workItem.setType(workItemDto.getType());
        }
        if (workItemDto.getType() != null) {
            workItem.setType(workItemDto.getType());
        }
        if (workItemDto.getText() != null) {
            workItem.setText(workItemDto.getText());
        }
        if (workItemDto.getXPosition() != null) {
            workItem.setXPosition(workItemDto.getXPosition());
        }
        if (workItemDto.getYPosition() != null) {
            workItem.setYPosition(workItemDto.getYPosition());
        }
        if (workItemDto.getWidth() != null) {
            workItem.setWidth(workItemDto.getWidth());
        }
        if (workItemDto.getHeight() != null) {
            workItem.setHeight(workItemDto.getHeight());
        }
        if (workItemDto.getFree() != null) {
            workItem.setFree(workItemDto.getFree());
        }
        if (workItemDto.getPage() != null) {
            workItem.setPage(workItemDto.getPage());
        }
        if (workItemDto.getFontSize() != null) {
            workItem.setFontSize(workItemDto.getFontSize());
        }
        if (workItemDto.getFontStyle() != null) {
            workItem.setFontStyle(workItemDto.getFontStyle());
        }
        if (workItemDto.getAutoCreated() != null) {
            workItem.setAutoCreated(workItemDto.getAutoCreated());
        }
        if (workItemDto.getFinished() != null) {
            workItem.setFinished(workItemDto.getFinished());
        }
        //WorkItem 엔티티를 저장하고 업데이트된 엔티티를 WorkItemDto로 변환하여 반환한다.
       WorkItem updatedWorkItem = workItemRepository.save(workItem);
        //Work의 업데이트 날짜를 현재 시간으로 설정한다.
        Work work = workItem.getWork();
        work.setUpdateDate(LocalDateTime.now());
        workRepository.save(work);

        updateWorkFinishStatus(workItem.getWork().getId());

        return workItemMapper.toDto(updatedWorkItem);
    }


@Transactional
public void deleteWorkItem(Long workItemId) {
    WorkItem workItem = workItemRepository.findById(workItemId)
            .orElseThrow(() -> new IllegalArgumentException("WorkItem not found"));
    workItemRepository.delete(workItem);
}
    @Transactional
    public List<WorkItemDto> getWorkItemsByWorkIdAndOtherUserId(Long workId, Long otherId) {
        List<WorkItem> workItems = workItemRepository.findByWorkIdAndUserIdAndAutoCreatedFalse(workId, otherId);

        return workItems.stream()
                .map(workItem -> {
                    WorkItemDto dto = workItemMapper.toDto(workItem);
                    // Sign 정보를 Sign ID로만 설정하여 오류 방지
                    dto.setSignId(workItem.getSign() != null ? workItem.getSign().getId() : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void deleteWorkItemsByUserAndWork(Long userId, Long workId) {
        List<WorkItem> workItems = workItemRepository.findByUserIdAndWorkId(userId, workId);

        if (workItems.isEmpty()) {
            throw new IllegalArgumentException("No WorkItems found for the user in this work.");
        }
        workItemRepository.deleteAll(workItems);

    }
    // 작업 항목 완료 상태 업데이트
    @Transactional
    public  void updateWorkFinishStatus(Long workId) {
        List<WorkItem> workItems = workItemRepository.findByWorkIdAndAutoCreatedFalse(workId);
        boolean allFinished = workItems.stream().allMatch(WorkItem::getFinished);

        Optional<Work> workOpt = workRepository.findById(workId);
        if (workOpt.isPresent()) {
            Work work = workOpt.get();
            work.setFinish(allFinished);
            workRepository.save(work);
        }
    }


}
