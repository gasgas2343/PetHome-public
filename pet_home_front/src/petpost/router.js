// 中文註解：PetPost 模組路由。
// 這裡集中管理論壇、毛孩時間軸、通知與論壇後台路由，避免和 member / wash 模組混在一起。
const PetPostWrapper = () =>
  import('@/petpost/components/forum/PetPostWrapper.vue')

export const petpostRoutes = [

  // ======================
  // 論壇首頁
  // ======================
  {
    path: '/community',
    component: PetPostWrapper,
    children: [
      {
        path: '',
        name: 'CommunityHome',
        component: () =>
          import('@/petpost/views/community/CommunityHomeView.vue'),
      },
      {
        path: 'posts',
        name: 'CommunityPosts',
        component: () =>
          import('@/petpost/views/community/CommunityView.vue'),
      },
      {
        path: 'ads',
        name: 'ForumAds',
        component: () =>
          import('@/petpost/views/product/ProductListView.vue'),
      },
    ],
  },

  // ======================
  // 文章
  // ======================
  {
    path: '/post',
    component: PetPostWrapper,
    children: [
      {
        path: 'create',
        name: 'PostCreate',
        component: () =>
          import('@/petpost/views/postView/PostViewCreate.vue'),
      },
      {
        path: ':postId',
        name: 'PostDetail',
        component: () =>
          import('@/petpost/views/postView/PostViewDetail.vue'),
      },
      {
        path: 'update/:postId',
        name: 'PostUpdate',
        component: () =>
          import('@/petpost/views/postView/PostViewUpdate.vue'),
      },
    ],
  },

  // ======================
  // 毛孩
  // ======================
  {
    path: '/pets',
    component: PetPostWrapper,
    children: [
      {
        path: '',
        name: 'PetList',
        component: () =>
          import('@/petpost/views/pet/PetListView.vue'),
      },
      {
        path: 'profile',
        name: 'PetProfile',
        component: () =>
          import('@/petpost/views/pet/PetProfileView.vue'),
      },
      {
        path: ':petId/timeline',
        name: 'PetTimeline',
        component: () =>
          import('@/petpost/views/pet/PetTimelineView.vue'),
      },
    ],
  },

  // ======================
  // 通知
  // ======================
  {
    path: '/notifications',
    component: PetPostWrapper,
    children: [
      {
        path: '',
        name: 'Notifications',
        component: () =>
          import('@/petpost/views/notification/NotificationView.vue'),
      },
    ],
  },

  // ======================
  // 後台 (維持原本)
  // ======================
  
  {
    path: '/forum-admin',
    component: () => import('@/petpost/admin/ForumAdminLayout.vue'),
    meta: {
      petpost: true,
      requiresAuth: true,
      requiresForumAdmin: true,
    },
    children: [
      { path: '', redirect: '/forum-admin/posts' },
      {
        path: 'posts',
        name: 'ForumAdminPosts',
        component: () => import('@/petpost/admin/ForumPostAdminView.vue'),
      },
      {
        path: 'comments',
        name: 'ForumAdminComments',
        component: () => import('@/petpost/admin/ForumCommentAdminView.vue'),
      },
      {
        path: 'reports',
        name: 'ForumAdminReports',
        component: () => import('@/petpost/admin/ForumReportAdminView.vue'),
      },
      {
        path: 'appeals',
        name: 'ForumAdminAppeals',
        component: () => import('@/petpost/admin/ForumAppealAdminView.vue'),
      },
      {
        path: 'notifications',
        name: 'ForumAdminNotifications',
        component: () => import('@/petpost/admin/ForumNotificationAdminView.vue'),
      },
    ],
  },

  { path: '/forum', redirect: '/community' },
  { path: '/articles', redirect: '/community' },
  { path: '/booking', redirect: '/pets' },
  { path: '/merchants', redirect: '/' },
  { path: '/events', redirect: '/' },
  { path: '/blog', redirect: '/' },
  { path: '/about', redirect: '/' },
  { path: '/news', redirect: '/' },
  { path: '/partners', redirect: '/' },
  { path: '/faq', redirect: '/' },
  { path: '/contact', redirect: '/' },
  { path: '/terms', redirect: '/' },
  { path: '/privacy', redirect: '/' },
]
