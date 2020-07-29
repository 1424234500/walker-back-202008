<template>
  <div class="app-container" >

    <el-row>
      <el-button>默认按钮</el-button>
      <el-button type="primary">primary按钮</el-button>
      <el-button type="success">success按钮</el-button>
      <el-button type="info">info按钮</el-button>
      <el-button type="warning">warning按钮</el-button>
      <el-button type="danger">danger按钮</el-button>
    </el-row>


    <el-row>
      <el-button plain>朴素按钮</el-button>
      <el-button type="primary" plain>主要按钮</el-button>
      <el-button type="success" plain>成功按钮</el-button>
      <el-button type="info" plain>信息按钮</el-button>
      <el-button type="warning" plain>警告按钮</el-button>
      <el-button type="danger" plain>危险按钮</el-button>
    </el-row>

    <el-row>
      <el-button round>圆角按钮</el-button>
      <el-button type="primary" round>主要按钮</el-button>
      <el-button type="success" round>成功按钮</el-button>
      <el-button type="info" round>信息按钮</el-button>
      <el-button type="warning" round>警告按钮</el-button>
      <el-button type="danger" round>危险按钮</el-button>
    </el-row>

    <el-row>
      <el-button icon="el-icon-search" circle></el-button>
      <el-button type="primary" icon="el-icon-edit" circle></el-button>
      <el-button type="success" icon="el-icon-check" circle></el-button>
      <el-button type="info" icon="el-icon-message" circle></el-button>
      <el-button type="warning" icon="el-icon-star-off" circle></el-button>
      <el-button type="danger" icon="el-icon-delete" circle></el-button>
    </el-row>


    <el-row>
      <el-button disabled>默认按钮</el-button>
      <el-button type="primary" disabled>主要按钮</el-button>
      <el-button type="success" disabled>成功按钮</el-button>
      <el-button type="info" disabled>信息按钮</el-button>
      <el-button type="warning" disabled>警告按钮</el-button>
      <el-button type="danger" disabled>危险按钮</el-button>
    </el-row>

    <el-row>
      <el-button plain disabled>朴素按钮</el-button>
      <el-button type="primary" plain disabled>主要按钮</el-button>
      <el-button type="success" plain disabled>成功按钮</el-button>
      <el-button type="info" plain disabled>信息按钮</el-button>
      <el-button type="warning" plain disabled>警告按钮</el-button>
      <el-button type="danger" plain disabled>危险按钮</el-button>
    </el-row>


    <el-button type="primary" icon="el-icon-edit"></el-button>
    <el-button type="primary" icon="el-icon-share"></el-button>
    <el-button type="primary" icon="el-icon-delete"></el-button>
    <el-button type="primary" icon="el-icon-search">搜索</el-button>
    <el-button type="primary">上传<i class="el-icon-upload el-icon--right"></i></el-button>


    <el-button-group>
      <el-button type="primary" icon="el-icon-arrow-left">上一页</el-button>
      <el-button type="primary">下一页<i class="el-icon-arrow-right el-icon--right"></i></el-button>
    </el-button-group>
    <el-button-group>
      <el-button type="primary" icon="el-icon-edit"></el-button>
      <el-button type="primary" icon="el-icon-share"></el-button>
      <el-button type="primary" icon="el-icon-delete"></el-button>
    </el-button-group>


    <el-button type="primary" :loading="true">加载中</el-button>

    <el-row>
      <el-button>默认按钮</el-button>
      <el-button size="medium">中等按钮</el-button>
      <el-button size="small">小型按钮</el-button>
      <el-button size="mini">超小按钮</el-button>
    </el-row>
    <el-row>
      <el-button round>默认按钮</el-button>
      <el-button size="medium" round>中等按钮</el-button>
      <el-button size="small" round>小型按钮</el-button>
      <el-button size="mini" round>超小按钮</el-button>
    </el-row>




    <div>
      <span>图片相关 lazy插件</span>
      <p>
key	description	default	options
preLoad	proportion of pre-loading height	1.3	Number
error	src of the image upon load fail	'data-src'	String
loading	src of the image while loading	'data-src'	String
attempt	attempts count	3	Number
listenEvents	events that you want vue listen for	['scroll', 'wheel', 'mousewheel', 'resize', 'animationend', 'transitionend', 'touchmove']	Desired Listen Events
adapter	dynamically modify the attribute of element	{ }	Element Adapter
filter	the image's listener filter	{ }	Image listener filter
lazyComponent	lazyload component	false	Lazy Component
dispatchEvent	trigger the dom event	false	Boolean
throttleWait	throttle wait	200	Number
observer	use IntersectionObserver	false	Boolean
observerOptions	IntersectionObserver options	{ rootMargin: '0px', threshold: 0.1 }	IntersectionObserver
silent	do not print debug info	true	Boolean
</p>
      <el-row>
        <span>vlazy-url</span>
        <span  v-for="item in urls">
          <div>{{item}}</div>
          <div><img style="width:200px;" v-lazy="item" ></div>
        </span>
        <span>
          <div>src-404: @/assets/404_images/404.png</div>
          <div><img style="width:200px;" src="@/assets/404_images/404.png" ></div>
        </span>

      </el-row>



    </div>


  </div>
</template>

<script>
  export default {
    data() {
      return {
       fits: ['fill', 'contain', 'cover', 'none', 'scale-down']
       ,url: 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg'
       ,src: 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg'
       ,urls: [
         'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg'
       , 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg'
       , "@/assets/404_images/404.png"
       , 'err']

        ,form: {
          name: '',
          region: '',
          date1: '',
          date2: '',
          delivery: false,
          type: [],
          resource: '',
          desc: ''
        }
      }
    },
    methods: {
      onSubmit() {
        this.$message('submit!')
      },
      onCancel() {
        this.$message({
          message: 'cancel!',
          type: 'warning'
        })
      }
    }
  }
</script>

<style scoped>
  .line{
    text-align: center;
  }
</style>

