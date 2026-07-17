import { userAuthStore } from '@/stores/auth'
import LoginView from '@/views/auth/LoginView.vue'
import RegisterView from '@/views/auth/RegisterView.vue'
import HomeView from '@/views/HomeView.vue'
import { createRouter, createWebHistory } from 'vue-router'
//  寵物論壇首頁元件
import { petpostRoutes } from '@/petpost/router'

import ShopLayout from '@/components/layout/ShopLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // ── pet_home 原有路由 ──
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/two-factor-verify',
      name: 'TwoFactorVerify',
      component: () => import('@/views/auth/TwoFactorVerifyView.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
    },
    // 論壇跳轉
    ...petpostRoutes,
    {
      path: '/member',
      component: () => import('@/views/member/MemberLayout.vue'),
      meta: {
        requiresAuth: true,
      },
      children: [
        { path: '', redirect: '/member/profile' },
        {
          path: 'profile',
          name: 'MemberProfile',
          component: () => import('@/views/member/MemberProfileView.vue'),
        },
        {
          path: 'pets',
          name: 'MemberPets',
          component: () => import('@/views/member/MemberPetsView.vue'),
        },
        {
          path: 'security',
          name: 'MemberSecurity',
          component: () => import('@/views/member/MemberSecurityView.vue'),
        },
      ],
    },
    {
      path: '/forgot-password',
      name: 'ForgotPassword',
      component: () => import('@/views/auth/ForgotPasswordView.vue'),
    },
    {
      path: '/reset-password',
      name: 'ResetPassword',
      component: () => import('@/views/auth/ResetPasswordView.vue'),
    },
    {
      path: '/register-email-sent',
      name: 'RegisterEmailSent',
      component: () => import('@/components/auth/RegisterEmailSentView.vue'),
    },
    {
      path: '/verify-email',
      name: 'VerifyEmail',
      component: () => import('@/components/auth/VerifyEmailView.vue'),
    },
    {
      path: '/admin',
      component: () => import('@/views/admin/AdminLayout.vue'),
      meta: {
        requiresAuth: true,
        requiresBackstage: true,
      },
      children: [
        {
          path: '',
          redirect: '/admin/users',
        },
        {
          path: 'users',
          name: 'AdminUsers',
          component: () => import('@/views/admin/AdminUsersView.vue'),
        },
        {
          path: 'roles',
          name: 'AdminRoles',
          component: () => import('@/views/admin/AdminRolesView.vue'),
        },
        {
          path: 'audit-logs',
          name: 'AdminAuditLogs',
          component: () => import('@/views/admin/AdminAuditLogsView.vue'),
        },
        {
          path: 'login-attempts',
          name: 'AdminLoginAttempts',
          component: () => import('@/views/admin/AdminLoginAttemptsView.vue'),
        },
        {
          path: 'sessions',
          name: 'AdminSessions',
          component: () => import('@/views/admin/AdminSessionsView.vue'),
        },
        // =========================
        // Wash 管理功能
        // =========================
        {
          path: 'wash/calendar',
          name: 'AdminWashCalendar',
          meta: { requiredRoles: ['ADMIN'] },
          component: () => import('@/views/wash/admin/admin_calendar.vue'),
        },
        {
          path: 'wash/notifications',
          name: 'AdminWashNotifications',
          meta: { requiredRoles: ['ADMIN'] },
          component: () => import('@/views/wash/admin/admin_notifications.vue'),
        },
        {
          path: 'wash/points-expiry',
          name: 'AdminWashPointsExpiry',
          meta: { requiredRoles: ['ADMIN'] },
          component: () => import('@/views/wash/admin/admin_points_expiry.vue'),
        },
      ],
    },
    {
      path: '/secure/login',
      name: 'secure-login',
      component: () => import('@/views/secure/Login.vue'),
    },

    // ── 商城路由（全部共用 ShopLayout，自動顯示 ShopNavigation）──
    {
      path: '/',
      component: ShopLayout,
      children: [
        {
          path: 'shop',
          name: 'ShopHome',
          component: () => import('../views/ShopHome.vue'),
        },
        {
          path: 'favorites',
          name: 'Favorites',
          component: () => import('@/views/ShopFavoritesView.vue'),
        },
        {
          path: 'cart',
          name: 'Cart',
          component: () => import('@/views/ShopCartView.vue'),
        },
        {
          path: 'coupon',
          name: 'Coupon',
          component: () => import('@/views/ShopCouponView.vue'),
        },
        {
          path: 'orders',
          name: 'OrderList',
          component: () => import('@/views/order/ShopOrderList.vue'),
        },
        {
          path: 'orders/:orderId',
          name: 'OrderDetail',
          component: () => import('@/views/order/ShopOrderDetail.vue'),
        },
        {
          path: 'products/category/:id',
          name: 'ProductCategory',
          component: () => import('@/views/pages/ShopProductList.vue'),
          props: true,
        },
        {
          path: '/checkout',
          name: 'Checkout',
          component: () => import('@/views/order/ShopCheckout.vue'),
        },
        {
          path: 'products/brand/:id',
          name: 'ProductBrand',
          component: () => import('@/views/pages/ShopProductList.vue'), // 共用同一個元件
          props: true,
        },
        {
          path: 'shop/member',
          name: 'ShopMemberCenter',
          component: () => import('@/views/user/ShopMemberCenter.vue'),
        },
      ],
    },
    {
      path: '/payment',
      name: 'Payment',
      component: () => import('@/views/order/ShopPayment.vue'),
    },

    // ── 後台管理（不需要 ShopNavigation）──
    {
      path: '/shop-admin',
      component: () => import('@/views/admin/ShopAdminLayout.vue'),
      meta: { requiresAuth: true, requiresBackstage: true },
      children: [
        { path: '', redirect: '/shop-admin/products' },
        { path: 'products', component: () => import('@/views/admin/ShopAdminPanel.vue') },
        { path: 'coupons', component: () => import('@/views/admin/ShopAdminCoupon.vue') },
        { path: 'campaigns', component: () => import('@/views/admin/ShopFlashSaleManage.vue') },
        { path: 'orders', component: () => import('@/views/admin/ShopOrderManage.vue') },
        { path: 'payments', component: () => import('@/views/admin/ShopAdminPayment.vue') },
      ],
    },
    // =========================
    // Wash 會員功能
    // =========================
    {
      path: '/wash',
      redirect: '/wash/price-list',
    },
    {
      path: '/wash/price-list',
      name: 'WashPriceList',
      meta: { requiresAuth: true, requiredRoles: ['USER'] },
      component: () => import('@/views/wash/member/price_list.vue'),
    },
    {
      path: '/wash/calendar',
      name: 'WashCalendar',
      meta: { requiresAuth: true, requiredRoles: ['USER'] },
      component: () => import('@/views/wash/member/member_calendar.vue'),
    },
    {
      path: '/wash/record',
      name: 'WashRecord',
      meta: { requiresAuth: true, requiredRoles: ['USER'] },
      component: () => import('@/views/wash/member/member_appt_record.vue'),
    },
    {
      path: '/wash/payment',
      name: 'WashPayment',
      meta: { requiresAuth: true, requiredRoles: ['USER'] },
      component: () => import('@/views/wash/member/member_appt_payment.vue'),
    },
    {
      path: '/wash/history-order',
      name: 'WashHistoryOrder',
      meta: { requiresAuth: true, requiredRoles: ['USER'] },
      component: () => import('@/views/wash/member/member_history_order.vue'),
    },
    {
      path: '/wash/points',
      name: 'WashPoints',
      meta: { requiresAuth: true, requiredRoles: ['USER'] },
      component: () => import('@/views/wash/member/member_points.vue'),
    },

    // =========================
    // Wash 美容師功能
    // =========================
    {
      path: '/groomer/price-list',
      name: 'GroomerPriceList',
      meta: { requiresAuth: true, requiredRoles: ['STAFF'] },
      component: () => import('@/views/wash/groomer/groomer_price_list.vue'),
    },
    {
      path: '/groomer/calendar',
      name: 'GroomerCalendar',
      meta: { requiresAuth: true, requiredRoles: ['STAFF'] },
      component: () => import('@/views/wash/groomer/groomer_calendar.vue'),
    },
    {
      path: '/groomer/calendar-back',
      name: 'GroomerCalendarBack',
      meta: { requiresAuth: true, requiredRoles: ['STAFF'] },
      component: () => import('@/views/wash/groomer/groomer_calendar_back.vue'),
    },
    {
      path: '/groomer/record',
      name: 'GroomerApptRecord',
      meta: { requiresAuth: true, requiredRoles: ['STAFF'] },
      component: () => import('@/views/wash/groomer/groomer_appt_record.vue'),
    },
    {
      path: '/groomer/payment',
      name: 'GroomerPayment',
      meta: { requiresAuth: true, requiredRoles: ['STAFF'] },
      component: () => import('@/views/wash/groomer/groomer_appt_payment.vue'),
    },

    {
      path: '/403',
      name: 'Forbidden',
      component: () => import('@/views/Forbidden.vue'),
    },
    // 404
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFound.vue'),
    },
    // =========================
    // PetMap
    // =========================
    {
      path: '/petmap',
      name: 'PetMapHome',
      component: () => import('@/views/petmap/PlaceListView.vue'),
    },
    {
      path: '/petmap/places/:id',
      name: 'PetMapPlaceDetail',
      component: () => import('@/views/petmap/PlaceDetailView.vue'),
    },
    {
      path: '/petmap/favorites',
      name: 'PetMapFavorite',
      component: () => import('@/views/petmap/FavoriteView.vue'),
    },
    {
      path: '/petmap/my-submissions',
      name: 'PetMapMySubmission',
      component: () => import('@/views/petmap/MySubmissionView.vue'),
    },
    {
      path: '/petmap/my-submissions/:id',
      name: 'PetMapSubmissionDetail',
      component: () => import('@/views/petmap/SubmissionDetailView.vue'),
    },
    {
      path: '/petmap/my-submissions/:id/edit',
      name: 'PetMapEditSubmission',
      component: () => import('@/views/petmap/EditSubmissionView.vue'),
    },
    {
      path: '/petmap/create-submission',
      name: 'PetMapCreateSubmission',
      component: () => import('@/views/petmap/CreateSubmissionView.vue'),
    },
    {
      path: '/petmap/my-reports',
      name: 'PetMapMyReport',
      component: () => import('@/views/petmap/MyReportsView.vue'),
    },
    {
      path: '/petmap/my-itineraries',
      name: 'PetMapMyItinerary',
      component: () => import('@/views/petmap/itinerary/MyItinerariesView.vue'),
    },
    {
      path: '/petmap/itineraries/:id',
      name: 'PetMapItineraryDetail',
      component: () => import('@/views/petmap/itinerary/ItineraryDetailView.vue'),
    },
    {
      path: '/petmap/ai-recommend',
      name: 'PetMapAIRecommend',
      component: () => import('@/views/petmap/AIRecommendView.vue'),
    },

    // =========================
    // PetMap Merchant
    // =========================
    {
      path: '/petmap/merchant',
      name: 'PetMapMerchantDashboard',
      meta: { requiresAuth: true, requiredRoles: ['STAFF'] },
      component: () => import('@/views/petmap/merchant/MerchantDashboardView.vue'),
    },
    {
      path: '/petmap/merchant/place',
      name: 'PetMapMerchantPlace',
      meta: { requiresAuth: true, requiredRoles: ['STAFF'] },
      component: () => import('@/views/petmap/merchant/MerchantPlaceManageView.vue'),
    },
    {
      path: '/petmap/merchant/place-photos',
      name: 'PetMapMerchantPlacePhoto',
      meta: { requiresAuth: true, requiredRoles: ['STAFF'] },
      component: () => import('@/views/petmap/merchant/MerchantPlacePhotoManageView.vue'),
    },
    {
      path: '/petmap/merchant/tags',
      name: 'PetMapMerchantTag',
      meta: { requiresAuth: true, requiredRoles: ['STAFF'] },
      component: () => import('@/views/petmap/merchant/MerchantPlaceTagManageView.vue'),
    },
    {
      path: '/petmap/merchant/reviews',
      name: 'PetMapMerchantReview',
      meta: { requiresAuth: true, requiredRoles: ['STAFF'] },
      component: () => import('@/views/petmap/merchant/MerchantReviewManageView.vue'),
    },

    // =========================
    // PetMap Admin
    // =========================
    {
    path: '/petmap/admin',
    name: "PetMapAdminDashboard",
    meta: { requiredRoles: ['ADMIN'] },
    component: () =>
        import("@/views/petmap/admin/AdminDashboardView.vue"),
    },
    {
      path: '/petmap/admin/place',
      name: 'PetMapAdminPlace',
      meta: { requiredRoles: ['ADMIN'] },
      component: () => import('@/views/petmap/admin/AdminPlaceManageView.vue'),
    },
    {
      path: '/petmap/admin/submissions',
      name: 'PetMapAdminSubmission',
      meta: { requiredRoles: ['ADMIN'] },
      component: () => import('@/views/petmap/admin/AdminSubmissionManageView.vue'),
    },
    {
      path: '/petmap/admin/reports',
      name: 'PetMapAdminReport',
      meta: { requiredRoles: ['ADMIN'] },
      component: () => import('@/views/petmap/admin/AdminReportManageView.vue'),
    },
  ],
})

// LINE PAY 導頁
// router.beforeEach(async (to, from, next) => {
//   const authStore = userAuthStore()

//   if (!authStore.isInitialized) {
//     await authStore.initAuth() // 等待恢復登入狀態完成
//   }

//   if (to.meta.requiresAuth && !authStore.accessToken) {
//     return next('/login')
//   }

//   next()
// })


router.beforeEach(async (to) => {
  const authStore = userAuthStore()

  if (to.meta.requiresAuth && !authStore.isLogin) {
    await authStore.initAuth()
  }
  if (to.meta.requiresAuth && !authStore.isLogin) {
    return '/login'
  }
  if (to.meta.requiresBackstage && !authStore.canAccessBackstage) {
    return '/'
  }
  // 當使用者試圖進入需要「論壇管理員」權限的頁面，但其身份並非管理員時，系統會將其強制重導向至社群首頁。
  if (to.meta.requiresForumAdmin && !authStore.hasRoles('ADMIN') && !authStore.hasRoles('STAFF')) {
    return '/community'
  }

  const requiredRoles = to.meta.requiredRoles
  if (
    Array.isArray(requiredRoles) &&
    requiredRoles.length > 0 &&
    !requiredRoles.some((role) => authStore.roles.includes(role))
  ) {
    return '/'
  }
})

export default router
