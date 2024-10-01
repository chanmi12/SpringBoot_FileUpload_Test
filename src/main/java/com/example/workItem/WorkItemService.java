package com.example.workItem;

import com.example.user.UserDto;
import com.example.user.UserRepository;
import com.example.work.repository.WorkRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.user.User;
import com.example.work.entity.Work;

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



    public WorkItemDto inviteUserToWork(Long workId, UserDto userDto){ //작업에 새로운 사용자 초대
        // Step 1: Find the work entity
        Work work = workRepository.findById(workId).orElseThrow(()-> new IllegalArgumentException("Work not found"));
        // Step 2: Check if the user already exists by email
        User user = userRepository.findByEmail(userDto.getEmail()).orElseGet(()->{
            // Step 3: If the user does not exist, create a new user
            User newUser = new User();
            newUser.setName(userDto.getName());
            newUser.setEmail(userDto.getEmail());
            return userRepository.save(newUser);
        });
        // Step 4: Create the WorkItem for the existing or newly created user
        WorkItem workItem = new WorkItem(work, user);
        workItem.setSignId(0L); //나중에 변경 해야함
        workItem.setType(2); //일반 유저들
        // Step 5: Save the WorkItem to the repository
        workItemRepository.save(workItem);
        // Step 6: Return the WorkItemDto
        return workItemMapper.toDto(workItem);
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

    public List<UserDto> listUniqueUsersForWork(Long workId){  //작업에 초대된 모든 고유 사용자 나열(중복 없음)
        List<WorkItem> workItems = workItemRepository.findByWorkId(workId);
        return workItems.stream()
                .map(WorkItem :: getUser)
                .distinct()
                .map(user -> new UserDto(user.getId(), user.getName(), user.getEmail()))
                .collect(Collectors.toList());
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
        workItemRepository.save(workItem);
        //Return the updated WorkItemDto
        return workItemMapper.toDto(workItem);
    }
    public void deleteWorkItem(Long workItemId){  //ID로 작업 항목 삭제

         if (!workItemRepository.existsById(workItemId)){
            throw new IllegalArgumentException("WorkItem not found");
        }
         workItemRepository.deleteById(workItemId);
    }
}
