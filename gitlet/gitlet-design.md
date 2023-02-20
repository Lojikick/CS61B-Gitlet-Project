# Gitlet Design Document
author:Avik Samanta

## Big Questions for TAS:

####Old Qs:
Ideas on implementing Hash/Treemaps and where?

How to store commits and blobs - A HASH MAP / TREE??

How is the branch system gonna be created? Is it just pointers??

###Main Questions:
//STORAGE: Am I on a good Track with how I'm handling storage? I'm using repositories for each major structure
//(blobs, files, commits) and using a hashmap to store their Sha1 ids and how they change

//TREES: What's up with them? The Spec was going on about how theyre a major structure that links commits,
//blobs, etc. but after watching the intro vids and seeing diagrams with the Sha1 codes, they seem to be
// more like pointers, ( Ala master and Head branch pointing to the sha1 codes of their commits ) is this right?
// Please clarify.
    
//COMMITS: Should I give the commit a Sha1 id instance variable?

//STAGE: IS AN ARRAYLIST A GOOD IDEA TO REPRESENT THE STAGES? 
//ARE ARRAYLISTS OK OR SHOULD I USE A HASHMAP INSTEAD? - Apologies for Caps lock lmao


## Design Document Guidelines

Please use the following format for your Gitlet design document. Your design
document should be written in markdown, a language that allows you to nicely 
format and style a text file. Organize your design document in a way that 
will make it easy for you or a course-staff member to read.  

## 1. Classes and Data Structures

Include here any class definitions. For each class list the instance
variables and static variables (if any). Include a ***brief description***
of each variable and its purpose in the class. Your explanations in
this section should be as concise as possible. Leave the full
explanation to the following sections. You may cut this section short
if you find your document is too wordy.

###Main.java

The Entry Point of the Gitlet Program, manipulating main data structures and handles user inputs.
Calls functions from other classes to deal with user inputs.

####Main internals and their data structures(Fields):
  _CWD: - current working directory

  _master: - GitletRepo Object that serves as the main repository of the control system:
             contains the staging area, storage/ tracking directories of the major files/ internals,
             and houses all of the Major commands,

  Original Vauge Plans for Data Strucutres:

 `Blobs - File Data Structure: Copy of the File holding the inputted file's contents

  Tree - TreeMap Data Structure: Maps names to references - Its an idea but I think the trees 
         Themselves might stem from the Commits? and jsut revolve around the hascodes instead
         

  Commits - Either a Linked List or an Array List Data Structure, Houses Metadata, Copies of Files
            And pointers to Parents and blorbs, (represented by hashcodes)


###Commits.java
An object representing Combinations of log messages, other metadata (commit date, author, etc.),
a reference to a tree, and references to parent commits.

Houses the current commit, pointers to previous commits and blobs
####Main internals and their data structures(Fields):
  
  _message: String containing the input message

  _parent: String instance of a shacode of the parent of the previous commit.

  _timestamp: String containing the time at which the commit was created

  _id: String containing the unique shacode of the commit.

  _blobs: Treemap containing the shacodes of blobs

  _metadata: File containing the commits metadata: Time created, et .

###Blob.java( Not fully Felshed out yet )

Houses the contents of the files
####Main internals and their data structures(Fields):

  _data: File that serves as a copy of the real file, holding a verson of its contents

###GitletRepo.java

Main manager of Gitlet, holds the commands, Houses the main Gitlet Repo and Stage,
And the storage directories for commits, blobs, files, and snapshots

####Main internals and their data structures(Fields):

_CWD - the current working Directory

_gitletRepo - File object (Directory) representing the main Gitlet Repository Directory

_staging area - The Staging area, represented by a Stage object

_masterBranch - The Master Branch, a String representing the Sha1 code of the pointing commit

_headFile - The Head Branch, a File representing the pathfile to the head commit

_FileDir - The File Directory, housing a Tree Map sotrage system that keeps track of Files Committed

_CommitDir - The Commit Directory, housing a Tree Map storage system that keeps track of Commit Objects in the Branch

_BlorbDir - The Blorb Directory, housing a Tree Map storage system that keeps track of Blorb Objects linked to the Branch

_SnapshotDir - The Snapshot Directory, housing a Tree Map storage system that keeps track of full Commit Snapshots

_BranchDir - The Branch Directory, housing files that store and save the head commit and the branches

_date - LocalDateTime object that gives the current date

_format - DateTimeFormatter object that converts the current date to a string format
###Stage.java

Where files get staged to be added or removed

####Main internals and their data structures(Fields):

  _addStage - Arraylist that holds files to be added

  _removeStage - Arraylist that holds files to be removed

###Storage Directory.java

An object that serves as a directory for different internals (Blorbs, COmmits, etc.)
And houses commands to update, add, and create new directories using the objects sha1 id and filename

####Main internals and their data structures(Fields):

_dirName - String instnance that houses the name given by the user

_thisDir - the File object representing the pathway for the newley created directory

## 2. Algorithms

This is where you tell us how your code works. For each class, include
a high-level description of the methods in that class. That is, do not
include a line-by-line breakdown of your code, but something you would
write in a javadoc comment above a method, ***including any edge cases
you are accounting for***. We have read the project spec too, so make
sure you do not repeat or rephrase what is stated there.  This should
be a description of how your code accomplishes what is stated in the
spec.

###GitletRepo.java

####Implemented/ In Progress:

-----
####Main Commands:

-----
public static void init() - Creates the Gitlet Repository, the Staging Area, and the First commit to the Working branch

public static void add(String name) - Get the name of the file inputted, sends that file to the staging area

public static void commit("string") - Saves a snapshot of tracked files in the current commit and staging area so they can be restored at a later time, creating a new commit.Saves the file in a history data structure within the stage and the current commit,
and creates a new commit from the files in the addition stage.

-----
####Helper/Setup Commands:

public GitletRepo() - Initializes the Current Working Directory

public static void setup() - Initializes the gitlet repository, the staging area, and all of the Storage Repositories responsible for tracking files, blobs, and commit snapshots

public static void initFDir() - Initializes the File Storage repository to Track Files, Uses a Treemap for storage

public static void initBDir() - Initializes the Blob Storage repository to Track Blobs, Uses a Treemap for storage

public static void initCDir() - Initializes the Commit Storage repository to Track Commits, Uses a Treemap for storage

public static void initSnapDir() - Initializes the Snapshot Storage repository to Track Full Commit Snapshots, Uses a Treemap for storage

public static void initBranchDir() - Initializes the Branch Storage repository to Track branches and Save the headCommit

public static void newBDr(String name, String id) - Creates a new branch directory for a new branch within the main branch directory

public static void updateHead(String path) - updates the head pathfile in the branch directory with a given pathfile of the new vommit

public static void update Branch(String name, String id) - updates the sha1 id of a given branch with a new given id in the branchDirectory

public static void updateDir(Object input,File dir) - Updates the Contents of a given Directory

public Object getFromDir(String code,File dir) - Gets the contents of a given Directory using a Sha1 code

getCommitPath(String commitid) - returns the filepath of a commit using its commit id


------

####TBD:
rm() - remove the file in the addition stage.

log() - STARTING FROM THE HEAD POSITION loops through the current branch, writes the metadata
and hash code within each commit in the file,
and prints it out once it reaches the very first commit

global-log() - STARTING FROM THE TOP loops through the current branch, writes the metadate within each commit in the file,
and hash code within each commit in the file,
and prints it out once it reaches the very first commit

find() - loops through the commit history and prints out the id that has the
commit message.

status()- gets the infromation from each data structure ( branches, staged files,removed files, etc) writes them
in a file and prints out the contents

status()- takes the version of the file in the head commit and overwrites that corresponding file in the CWD, or takes
all the files in the current branch and perform the same function.

branch()- creates a new branch pointer node and put it at the currenthead node commit

rmbranch()- removes the branch pointer node

reset(number commitId)-checks out all the files tracked by the given commit corresponding to the id,
and removes files not present in commit,

merge(String branch name) - Merges the files from the inputted branch into the current branch
finds common ancestor as the split point

Storages for each respective object:

_FileDir

_CommitDir

_BlorbDir

###Main.java

The length of this section depends on the complexity of the task and
the complexity of your design. However, simple explanations are
preferred. Here are some formatting tips:

* For complex tasks, like determining merge conflicts, we recommend
  that you split the task into parts. Describe your algorithm for each
  part in a separate section. Start with the simplest component and
  build up your design, one piece at a time. For example, your
  algorithms section for Merge Conflicts could have sections for:

  * Checking if a merge is necessary.
  * Determining which files (if any) have a conflict.
  * Representing the conflict in the file.

* Try to clearly mark titles or names of classes with white space or
  some other symbols.



###Commit.java


public Commit(String message, String parent) - Initializes the message, the parent commit, and the commit's Sha1 id and takes care of initial commit if no parent found

public File metadata() - Returns and initializes the metadata for a given commit

public String getMessage() - A getter method to obtain the Commit's message

public String getTimestamp() - A getter method to obtain the Commit's Timestamp

public String getId() -A getter method to obtain the Commit's Sha1 id

public String getParent() - A getter method to obtain the Commit's Parent

###Blorb.java

contents() - File copy that houses that houses the file's contents

getCopy() - function that gets the contents


###StagingArea.java

getItem(item, stage) gets an item from a stage

getAddStage() - returns the Addition Stage Arraylist

getRemoveStage() - returns the Removal Stage Arraylist

toAddStage() - adds a file to the addition stage

toRemoveStage() - adds a file to the removal stage

outAddStage() - removes a file from the addition stage

outRemoveStage() - removes a file from the removal stage

clearAddStage() - clears the additionstage;

clearRemoveStage() - clears the removalstage;

###StorageDirectory.java

newDr(String fileName) - inits a new directory

updateDr(String fileName, Object newObject) - updates a directory

getFromDr(String fileName, String code) - gets an object froma driectory (can use cast to get specific object)

getCommitDr(String fileName, String code) - returns a commit from a dir

getFileDr(String fileName, String code) - returns a file from a directory

getBlorbDr(String fileName, String code) - returns a blorb from a directory

## 3. Persistence

Describe your strategy for ensuring that you don’t lose the state of your program
across multiple runs. Here are some tips for writing this section:
* This section should be structured as a list of all the times you
  will need to record the state of the program or files. For each
  case, you must prove that your design ensures correct behavior. For
  example, explain how you intend to make sure that after we call
  `java gitlet.Main add wug.txt`,
  on the next execution of
  `java gitlet.Main commit -m “modify wug.txt”`,
  the correct commit will be made.

* A good strategy for reasoning about persistence is to identify which
  pieces of data are needed across multiple calls to Gitlet. Then,
  prove that the data remains consistent for all future calls.

* This section should also include a description of your .gitlet
  directory and any files or subdirectories you intend on including
  there.

#### (Old Idea ) SAVING THE FILES THROUGH COMMITS

There will be a instance called _trackingFile, a directory that tracks all the files commited. 
When a file is commited a new directory with that file's name will be created in _trackingFile
And each time that file is commited, the contents of the blorb with that file will be inserted
as a file to that file's current dirrectory.

### (Current Plan) GitletRepo and its many Subdirectories

The main repository Gitlet repo contains four main subdirectories for storage:

_FileDir - The File Directory, housing a Tree Map sotrage system that keeps track of Files Committed

_CommitDir - The Commit Directory, housing a Tree Map storage system that keeps track of Commit Objects in the Branch

_BlorbDir - The Blorb Directory, housing a Tree Map storage system that keeps track of Blorb Objects linked to the Branch

_SnapshotDir - The Snapshot Directory, housing a Tree Map storage system that keeps track of full Commit Snapshots

_BranchDir - The Branch Directory, housing files containing sha1 codes of pointers, and a head pathfile

They will all act as storage subdirectories managing Sha1 codes of their repective
internal structures. This is to ensure that older versions and current versions of files, commits, blorbs, etc
will be preserved and documented, and that the following commands can always pull a structure
by its Sha1 code. As such, the commands will also be activley updating the directories depending on the circumstances.


I wish to lock down my persistence and understanding of the relationships between these
structures before I go on deeper onto the project, I want to check for sure that
this method is a valid path forward, thanks!



## 4. Design Diagram

(Note, Wanna flesh this out a bit more)
Attach a picture of your design diagram illustrating the structure of your
classes and data structures. The design diagram should make it easy to 
visualize the structure and workflow of your program.




