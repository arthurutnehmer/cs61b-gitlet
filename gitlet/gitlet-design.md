# Gitlet Design Document
author: Arthur Utnehmer

## 1. Classes and Data Structures

### Tree
This class will be responsible for handling the data that will be pass in for commits. A tree of commits.

#### Fields

1. Node head


### Commit

This class will be responsible for defining the type of the node that will be used in a commit tree.

#### Instance Variables
* Time - Time of commit.
* Message - Message for commit.
* Parent - pointer to parent node. 
* Files - Pointers to each file 
* Commit Hash - Hash for commit


### Commit Tree

This class will be responsible for handling the commit tree. 

Including all metadata and references when hashing a commit.

#### Fields

#### Instance Variables
* Structure containing the nodes of the commit

### File organizer

This class will be responsible for assembling files (blobs) from the references 

#### Fields
1. File table

## 2. Algorithms

### Commit Tree

1. addCommitToTree(): Adds the commit to the tree.

### File organizer

1. getFilesFromCommit(): Will take in a commit and get files from the references.
2. prepareCommitFromFiles(): Will do a file snapshot and return a commit with files filled in.
3. AddFilesToTrack(): Will track files that are asked to track.

## 3. Persistence

Describe your strategy for ensuring that you don’t lose the state of your program
across multiple runs.

* This section should be structured as a list of all the times you
  will need to record the state of the program or files. For each
  case, you must prove that your design ensures correct behavior. For
  example, explain how you intend to make sure that after we call
       `java gitlet.Main add wug.txt`,
  on the next execution of
       `java gitlet.Main commit -m “modify wug.txt”`, 
  the correct commit will be made. 
  - To ensure file persistence, we must update the file organizer with the new files to return an updated commit 
before committing. This will ensure that files are saved correctly.
  
* To ensure that a commit tree is recoverable the following precaution must be taken:
  - When any update is made to the commit tree, the commit tree must be saved to file.
* To ensure that files are saved and recoverable for commits the following must be done:
  - When a file is updated and meant to be saved, it must be added to a commit and the reference
  - to the file must be saved. To recover a commit, and therefore its files, the references stored in the commit
  - must be used to recover the file. 
  
* This section should also include a description of your .gitlet
  directory and any files or subdirectories you intend on including
  there.
* The directory will be composed off
* util/staged
* util/commits

## 4. Design Diagram
![](/home/arthur/repo/proj3/gitlet/Untitled Workspace (1).png)


