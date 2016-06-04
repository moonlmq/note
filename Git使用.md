# Git 使用指南

### 1.Git的基本配置

用户信息（用户名和邮件是刚刚注册的）
```
git config --global user.name "Your Name Here"

git config --global user.email your_email@example.com
```
查看已有的配置信息：
```
git config --list
```

### 2.新建一个仓库（repository）

登录GitHub账户，点击New repository，填写仓库名后；
有两种方式来初始化仓库：在本地的工作目录初始化新仓库、从现有仓库克隆
（1）在本地的工作目录初始化新仓库
进入项目的目录下：
```
touch README.md

git init

git add *

git commit -m 'initial project version'

git remote add origin git@github.com:username/project_name.git  ## 即刚刚创建的仓库的地址

git push -u origin master	##推送代码到远程代码库
```
（2）从现有仓库克隆
```
git clone git_address
```

### 3.分支

（1） 创建分支
当你有什么内容需要添加，但又不确定的时候，或者需要创建项目的另一个独立功能时，你可以创建新的分支，独立于主分支，默认地每次提交的代码都更新到master主分支上。
创建并同时切换到新建分支上：
```
git checkout -b branch_name
```
也可以先创建一个分支再切换到新分支上：
```
git branch branch_name

git checkout branch_name
```
查看该项目的所有分支(包括远端)
```
git branch -a
```
注意：任何时候你都可以通过git checkout master切换到主分支上，而且你可以有多个分支，也可以在一个分支上再新建分支。

（2）合并分支

在你的新分支，你完成任务后用git add添加你新增的文件
然后
```
git add .git

git commit -m "add new feature" 

git checkout master	##切换到主分支

git merge branch_name	##将新分支的内容合并到主分支上，主分支和新分支就一样了
```

（3）删除分支
如果你觉得新分支没有用了，也不想保存了
在主分支上
```
git branch -d branch_name	##删除已经合并到主分支的branch

git branch -D branch_name	##删除branch分支，不管有没合并
```

（4）回到项目之前提交的版本（很实用）
查看提交记录：
```
git log
```
输出历史记录，像下面这样子：

commit ca82a6dff817ec66f44342007202690a93763949Author: your_username your_email@domain.comDate:   Mon Nov 4 12:52:11 2013 -0700    changes the frontpage layout
commit 085bb3bcb608e1e8451d4b2432f8ecbe6306e7e7Author: your_username your_email@domain.comDate:   Mon Nov 4 11:40:33 2013 -0700    adds my new feature
commit a11bef06a3f659402fe7563abf99ad00de2209e6Author: your_username your_email@domain.comDate:   Mon Nov 4 10:37:28 2013 -0700    initial commit

如果想回到" adds my new feature" 这个版本，简单地用提交的ID就可以了，ID的前几位（可以区分其他版本就可以了）
```
git log --oneline      ##用来查看每次提交的ID

git reset --hard commit id 
```

也可以使用`git checkout commit id`，但使用该命令会进入detached HEAD模式，只是临时的将HEAD指向某次commit，如果继续commit会产生类似分支的效果，但是如果不主动创建分支而直接切回其他分支，那么在detached HEAD模式下的更改都将会被丢弃，详见[git book](https://git-scm.com/docs/git-checkout)。

或者可以把这个版本变为一个新的独立分支：
```
git checkout -b brach_name 085bb3bcb
```

### 4.提交代码到远程代码库
```
git remote add origin git@github.com:username/project_name.git  ## 即刚刚创建的仓库的地址

git push -u origin master	##推送代码到远程代码库
```