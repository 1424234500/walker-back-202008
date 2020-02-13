import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes menu mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },

  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },

  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [{
      path: 'dashboard',
      name: 'Dashboard',
      component: () => import('@/views/dashboard/index'),
      meta: { title: 'Dashboard', icon: 'dashboard' }
    }]
  },

  {
    path: '/db',
    component: Layout,
    redirect: '/db/Student',
    name: 'Db',
    meta: { title: 'Tables', icon: 'table' },
    children: [
      {
        path: 'student',
        name: 'Student',
        component: () => import('@/views/db/student'),
        meta: { title: 'Student', icon: 'people' }
      },
      {
        path: 'teacher',
        name: 'Teacher',
        component: () => import('@/views/db/teacher'),
        meta: { title: 'Teacher', icon: 'user' }
      },
      {
        path: 'mtable',
        name: 'Mtable',
        component: () => import('@/views/db/mtable'),
        meta: { title: 'Mtable', icon: 'table',
          keepAlive: true ,//当前的.vue文件需要缓存
        }
      },
      {
        path: 'auto',
        name: 'Auto',
        component: () => import('@/views/db/auto'),
        meta: { title: 'Auto', icon: 'table' ,
          keepAlive: true ,//当前的.vue文件需要缓存
        },

      },
    ]
  },



  {
    path: '/user',
    component: Layout,
    redirect: '/user/user',
    name: 'User',
    meta: { title: 'User', icon: 'user' },
    children: [
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/user'),
        meta: { title: 'User', icon: 'people' }
      },
      {
        path: 'dept',
        name: 'Dept',
        component: () => import('@/views/user/dept'),
        meta: {title: 'Dept', icon: 'list'}
      },
      {
        path: 'depttree',
        name: 'DeptTree',
        component: () => import('@/views/user/depttree'),
        meta: { title: 'DeptTree', icon: 'tree' }
      },
      {
        path: 'area',
        name: 'Area',
        component: () => import('@/views/user/area'),
        meta: {title: 'Area', icon: 'list'}
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/user/role'),
        meta: {title: 'Role', icon: 'theme'}
      },
      {
          path: 'area',
          name: 'Area',
          component: () => import('@/views/user/area'),
          meta: {title: 'Area', icon: 'list'}
      },
      {
        path: 'areatree',
        name: 'AreaTree',
        component: () => import('@/views/user/areatree'),
        meta: { title: 'AreaTree', icon: 'tree' }
      },
      // {
      //   path: 'roleuser',
      //   name: 'RoleUser',
      //   component: () => import('@/views/user/roleuser'),
      //   meta: {title: 'RoleUser', icon: 'theme'}
      // },
    ]
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/quartz',
    name: 'System',
    meta: { title: 'System', icon: 'documentation' },
    children: [
      {
        path: 'quartz',
        name: 'Quartz',
        component: () => import('@/views/system/quartz'),
        meta: { title: 'Quartz', icon: 'tree-table' }
      },
      {
        path: 'tree',
        name: 'Tree',
        component: () => import('@/views/file/tree'),
        meta: { title: 'Tree', icon: 'tree' }
      },
    ]
  },
  {
    path: '/file',
    component: Layout,
    redirect: '/file/file',
    name: 'File',
    meta: { title: 'File', icon: 'documentation' },
    children: [
      {
        path: 'fileindex',
        name: 'Fileindex',
        component: () => import('@/views/file/fileindex'),
        meta: { title: 'Fileindex', icon: 'tree-table' }
      },
      {
        path: 'file',
        name: 'File',
        component: () => import('@/views/file/file'),
        meta: {title: 'File', icon: 'tab'}
      },
      {
        path: 'tree',
        name: 'Tree',
        component: () => import('@/views/file/tree'),
        meta: { title: 'Tree', icon: 'tree' }
      },
    ]
  },
  {
    path: '/echarts',
    component: Layout,
    redirect: '/echarts/controller',
    name: 'Echarts',
    meta: { title: 'Echarts', icon: 'chart' },
    children: [
      {
        path: 'controller',
        name: 'Controller',
        component: () => import('@/views/echarts/controller'),
        meta: { title: 'Controller', icon: 'tree-table' }
      },
      {
        path: 'socket',
        name: 'Socket',
        component: () => import('@/views/echarts/socket'),
        meta: { title: 'Socket', icon: 'tree' }
      }
    ]
  },

  {
    path: '/other',
    component: Layout,
    redirect: '/other/button',
    name: 'Other',
    meta: { title: 'Other', icon: 'form' },
    children: [
      {
        path: 'button',
        name: 'button',
        component: () => import('@/views/other/button'),
        meta: { title: 'Button', icon: 'tree-table' }
      },
      {
        path: 'tree',
        name: 'Tree',
        component: () => import('@/views/other/tree'),
        meta: {title: 'Tree', icon: 'tab'}
      },
      {
        path: 'popover',
        name: 'Popover',
        component: () => import('@/views/other/popover'),
        meta: { title: 'Popover', icon: 'tree' }
      },
      {
        path: 'form',
        name: 'Form',
        component: () => import('@/views/other/form'),
        meta: { title: 'Form', icon: 'tree' }
      },
    ]
  },

  {
    path: '/menu',
    component: Layout,
    redirect: '/menu/menu1',
    name: 'Menu',
    meta: {
      title: 'Menu',
      icon: 'nested'
    },
    children: [
      {
        path: 'menu1',
        component: () => import('@/views/menu/menu1/index'), // Parent router-view
        name: 'Menu1',
        meta: { title: 'Menu1' },
        children: [
          {
            path: 'menu1-1',
            component: () => import('@/views/menu/menu1/menu1-1'),
            name: 'Menu1-1',
            meta: { title: 'Menu1-1' }
          },
          {
            path: 'menu1-2',
            component: () => import('@/views/menu/menu1/menu1-2'),
            name: 'Menu1-2',
            meta: { title: 'Menu1-2' },
            children: [
              {
                path: 'menu1-2-1',
                component: () => import('@/views/menu/menu1/menu1-2/menu1-2-1'),
                name: 'Menu1-2-1',
                meta: { title: 'Menu1-2-1' }
              },
              {
                path: 'menu1-2-2',
                component: () => import('@/views/menu/menu1/menu1-2/menu1-2-2'),
                name: 'Menu1-2-2',
                meta: { title: 'Menu1-2-2' }
              }
            ]
          },
          {
            path: 'menu1-3',
            component: () => import('@/views/menu/menu1/menu1-3'),
            name: 'Menu1-3',
            meta: { title: 'Menu1-3' }
          }
        ]
      },
      {
        path: 'menu2',
        component: () => import('@/views/menu/menu2/index'),
        meta: { title: 'Menu2' }
      }
    ]
  },

  {
    path: 'external-link',
    component: Layout,
    children: [
      {
        path: 'https://baidu.com',
        meta: { title: 'External Link', icon: 'link' }
      }
    ]
  },

  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
